package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDAO;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;

public class ProductoController {
    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    @FXML private Button btnEliminar;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo, colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    private Producto seleccionado;
    private String rutaImagen;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tblProductos.setItems(productos);

        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> mostrar(actual));

        chkActivo.setSelected(true);
        btnEliminar.setDisable(true);

        cargarCategorias();
        cargarProductos();
    }

    // Carga en el ComboBox solo las categorías activas
    private void cargarCategorias() {
        try {
            cmbCategoria.setItems(FXCollections.observableArrayList(
                    categoriaDAO.listar().stream().filter(Categoria::isActiva).toList()));
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            productos.setAll(productoDAO.listar());
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    // Pasa el producto seleccionado en la tabla al formulario
    private void mostrar(Producto p) {
        seleccionado = p;
        btnEliminar.setDisable(p == null);
        if (p == null) return;

        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(p.getPrecioVenta().toPlainString());
        txtExistencia.setText(String.valueOf(p.getExistencia()));
        chkActivo.setSelected(p.isActivo());

        cmbCategoria.getItems().stream()
                .filter(c -> Objects.equals(c.getId(), p.getCategoria().getId()))
                .findFirst()
                .ifPresentOrElse(c -> cmbCategoria.setValue(c),
                        () -> cmbCategoria.getSelectionModel().clearSelection());

        rutaImagen = p.getRutaImagen();
        cargarImagen(rutaImagen);
    }

    private void cargarImagen(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            imgProducto.setImage(null);
            return;
        }
        try {
            imgProducto.setImage(new Image(ruta, true));
        } catch (IllegalArgumentException e) {
            imgProducto.setImage(null);
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            cargarImagen(rutaImagen);
        }
    }

    @FXML
    private void guardar() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());
            if (precio.signum() <= 0 || existencia < 0) {
                mensaje(Alert.AlertType.WARNING,
                        "Precio mayor que cero y existencia no negativa.");
                return;
            }

            Producto producto = new Producto(
                    seleccionado == null ? null : seleccionado.getId(),
                    txtCodigo.getText().trim(), txtNombre.getText().trim(), cmbCategoria.getValue(), precio,
                    existencia, rutaImagen, chkActivo.isSelected());

            if (seleccionado == null) {
                productoDAO.guardar(producto);
                mensaje(Alert.AlertType.INFORMATION, "Producto registrado correctamente.");
            } else {
                productoDAO.actualizar(producto);
                mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
            }
            nuevo();
            cargarProductos();
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (seleccionado == null) return;
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el producto \"" + seleccionado.getNombre() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirmar.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            productoDAO.eliminar(seleccionado.getId());
            nuevo();
            cargarProductos();
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        tblProductos.getSelectionModel().clearSelection();
        seleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
        btnEliminar.setDisable(true);
        txtCodigo.requestFocus();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
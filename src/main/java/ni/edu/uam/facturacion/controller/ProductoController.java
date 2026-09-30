package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.stage.Stage;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDAO;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;

public class ProductoController {
    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;
    @FXML private Button btnGuardar, btnActualizar, btnEliminar;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo, colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    private final FilteredList<Producto> productosFiltrados = new FilteredList<>(productos, p -> true);
    private final Categoria todasCategorias = new Categoria(null, "Todas las categorías", true);

    private Producto seleccionado;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tblProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tblProductos.setItems(productosFiltrados);

        cmbEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbEstado.setValue("Todos");
        cmbFiltroCategoria.setValue(todasCategorias);
        txtBuscar.textProperty().addListener((obs, a, n) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, a, n) -> aplicarFiltros());
        cmbFiltroCategoria.valueProperty().addListener((obs, a, n) -> aplicarFiltros());

        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> mostrar(actual));

        chkActivo.setSelected(true);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        cargarCategorias();
        cargarProductos();
    }

    // Carga en el ComboBox solo las categorías activas
    private void cargarCategorias() {
        try {
            var todas = categoriaDAO.listar();
            cmbCategoria.setItems(FXCollections.observableArrayList(
                    todas.stream().filter(Categoria::isActiva).toList()));
            ObservableList<Categoria> filtro = FXCollections.observableArrayList(todasCategorias);
            filtro.addAll(todas);
            cmbFiltroCategoria.setItems(filtro);
            cmbFiltroCategoria.setValue(todasCategorias);
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

    // Combina búsqueda (código, nombre, categoría), estado y categoría
    private void aplicarFiltros() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        String estado = cmbEstado.getValue();
        Categoria cat = cmbFiltroCategoria.getValue();

        productosFiltrados.setPredicate(p -> {
            boolean coincideTexto = texto.isEmpty()
                    || p.getCodigo().toLowerCase().contains(texto)
                    || p.getNombre().toLowerCase().contains(texto)
                    || p.getCategoria().getNombre().toLowerCase().contains(texto);
            boolean coincideEstado = estado == null || estado.equals("Todos")
                    || (estado.equals("Activos") == p.isActivo());
            boolean coincideCategoria = cat == null || cat.getId() == null
                    || Objects.equals(cat.getId(), p.getCategoria().getId());
            return coincideTexto && coincideEstado && coincideCategoria;
        });
    }

    // Pasa el producto seleccionado en la tabla al formulario
    private void mostrar(Producto p) {
        seleccionado = p;
        btnEliminar.setDisable(p == null);
        btnActualizar.setDisable(p == null);
        btnGuardar.setDisable(p != null);
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
    }

    @FXML
    private void guardar() {
        Producto producto = leerFormulario(null);
        if (producto == null) return;
        try {
            productoDAO.guardar(producto);
            mensaje(Alert.AlertType.INFORMATION, "Producto registrado correctamente.");
            nuevo();
            cargarProductos();
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (seleccionado == null) return;
        Producto producto = leerFormulario(seleccionado.getId());
        if (producto == null) return;
        try {
            productoDAO.actualizar(producto);
            mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
            nuevo();
            cargarProductos();
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    // Valida el formulario; devuelve null (tras mostrar el error) si hay datos incorrectos
    private Producto leerFormulario(Integer id) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) return invalido("El código es obligatorio.");
        if (nombre.isEmpty()) return invalido("El nombre es obligatorio.");
        if (cmbCategoria.getValue() == null) return invalido("Debe seleccionar una categoría.");

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            return invalido("El precio debe ser numérico.");
        }
        if (precio.signum() <= 0) return invalido("El precio debe ser mayor que cero.");

        int existencia;
        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            return invalido("La existencia debe ser un número entero.");
        }
        if (existencia < 0) return invalido("La existencia no puede ser negativa.");

        boolean duplicado = productos.stream().anyMatch(p ->
                p.getCodigo().equalsIgnoreCase(codigo) && !Objects.equals(p.getId(), id));
        if (duplicado) return invalido("Ya existe un producto con el código \"" + codigo + "\".");

        return new Producto(id, codigo, nombre, cmbCategoria.getValue(), precio,
                existencia, chkActivo.isSelected());
    }

    private Producto invalido(String texto) {
        mensaje(Alert.AlertType.WARNING, texto);
        return null;
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
        btnEliminar.setDisable(true);
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
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
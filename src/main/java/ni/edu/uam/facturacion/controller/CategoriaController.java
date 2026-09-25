package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.model.Categoria;

import java.sql.SQLException;

public class CategoriaController {
    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private Button btnEliminar;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private Categoria seleccionada;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
        tblCategorias.setItems(categorias);

        tblCategorias.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> mostrar(actual));

        chkActiva.setSelected(true);
        btnEliminar.setDisable(true);
        cargarCategorias();
    }

    private void cargarCategorias() {
        try {
            categorias.setAll(categoriaDAO.listar());
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    private void mostrar(Categoria c) {
        seleccionada = c;
        btnEliminar.setDisable(c == null);
        if (c == null) return;
        txtNombre.setText(c.getNombre());
        chkActiva.setSelected(c.isActiva());
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mensaje(Alert.AlertType.WARNING, "El nombre es obligatorio.");
            return;
        }
        try {
            if (seleccionada == null) {
                categoriaDAO.guardar(new Categoria(null, nombre, chkActiva.isSelected()));
                mensaje(Alert.AlertType.INFORMATION, "Categoría registrada.");
            } else {
                categoriaDAO.actualizar(new Categoria(seleccionada.getId(), nombre, chkActiva.isSelected()));
                mensaje(Alert.AlertType.INFORMATION, "Categoría actualizada.");
            }
            nuevo();
            cargarCategorias();
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (seleccionada == null) return;
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar la categoría \"" + seleccionada.getNombre() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirmar.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            categoriaDAO.eliminar(seleccionada.getId());
            nuevo();
            cargarCategorias();
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        tblCategorias.getSelectionModel().clearSelection();
        seleccionada = null;
        txtNombre.clear();
        chkActiva.setSelected(true);
        btnEliminar.setDisable(true);
        txtNombre.requestFocus();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
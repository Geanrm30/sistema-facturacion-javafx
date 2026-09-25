package ni.edu.uam.facturacion.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ni.edu.uam.facturacion.util.DatabaseConnection;
import ni.edu.uam.facturacion.util.SceneManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MenuPrincipalController {
    @FXML
    private void abrirProductos() {
        abrir("/ni/edu/uam/facturacion/fxml/producto-view.fxml", "Gestión de productos");
    }

    @FXML
    private void abrirCategorias() {
        abrir("/ni/edu/uam/facturacion/fxml/categoria-view.fxml", "Gestión de categorías");
    }

    @FXML
    private void probarConexion() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            new Alert(Alert.AlertType.INFORMATION, "Conexión exitosa.").showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Error de conexión: " + e.getMessage()).showAndWait();
        }
    }

    private void abrir(String recurso, String titulo) {
        try {
            SceneManager.abrirVentana(recurso, titulo);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir " + titulo + ".").showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK)
            Platform.exit();
    }
}
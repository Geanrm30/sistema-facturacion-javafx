package ni.edu.uam.facturacion.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/tienda_javafx";

    private static final String USER =
            "postgres";

    // La contraseña se lee de la variable de entorno DB_PASSWORD
    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection()
            throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException("Falta la variable de entorno DB_PASSWORD.");
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}
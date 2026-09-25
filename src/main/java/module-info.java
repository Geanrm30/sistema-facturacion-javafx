module ni.edu.uam.facturacion {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    exports ni.edu.uam.facturacion.application;
    exports ni.edu.uam.facturacion.model;
    opens ni.edu.uam.facturacion.controller to javafx.fxml;
    opens ni.edu.uam.facturacion.model to javafx.base;
}

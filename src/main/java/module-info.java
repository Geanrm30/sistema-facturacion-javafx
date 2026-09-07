module ni.edu.uam.facturacionapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.facturacionapp to javafx.fxml;
    exports ni.edu.uam.facturacionapp;
}
module ni.edu.uam.facturacionapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.uam.facturacionapp to javafx.fxml;
    exports ni.edu.uam.facturacionapp;
}
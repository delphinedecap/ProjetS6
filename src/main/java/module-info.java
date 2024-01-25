module com.triceratops.triceratops {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    opens com.triceratops.triceratops to javafx.fxml;
    exports com.triceratops.triceratops;
    exports com.triceratops.triceratops.modele;
}
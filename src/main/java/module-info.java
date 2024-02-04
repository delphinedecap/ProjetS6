module com.triceratops.triceratops {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires MaterialFX;

    opens com.triceratops.triceratops to javafx.fxml;

    opens com.triceratops.triceratops.controllers to javafx.fxml;

    exports com.triceratops.triceratops;
    exports com.triceratops.triceratops.modele;
    exports com.triceratops.triceratops.controllers; // Ajoutez cette ligne pour exporter le package avec la classe Home
}
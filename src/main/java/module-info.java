module com.triceratops.triceratops {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.triceratops.triceratops to javafx.fxml;
    exports com.triceratops.triceratops;
}
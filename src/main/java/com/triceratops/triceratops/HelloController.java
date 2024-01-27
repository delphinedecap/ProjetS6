package com.triceratops.triceratops;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    public GridPane contentPane;

    /*@FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application! : Benjamin, Delphine et Jérémy");
    }*/

    public void initialize() throws IOException {
        GridPane screen = FXMLLoader.load(getClass().getResource("fxml/home.fxml"));
        contentPane.setAlignment(Pos.CENTER);
        contentPane.add(screen,0,0);
    }


    public void about(ActionEvent actionEvent) {
        try {
            //Create view from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/about.fxml"));

            //Create Stage
            Stage stage = new Stage();
            stage.setTitle("A propos - Triceratops");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Bloquer la redimension de la fenêtre
            stage.setResizable(false);

            //Set view in window
            stage.setScene(new Scene(loader.load(), 400, 200));

            //Launch
            stage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        };

    }
}
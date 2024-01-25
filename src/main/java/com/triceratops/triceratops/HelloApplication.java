package com.triceratops.triceratops;

import com.triceratops.triceratops.modele.Produit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.triceratops.triceratops.persistance.ProduitSerializer.serializeToFile;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello GitHub!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Produit test = new Produit(2,"TEST2","produit de test 2",20,10,"kg");
        serializeToFile(test);
        launch();
    }
}
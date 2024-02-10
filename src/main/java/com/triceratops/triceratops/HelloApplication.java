package com.triceratops.triceratops;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    /**
     *
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/root.fxml"));
        fxmlLoader.setControllerFactory(c -> new HelloController(stage));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("Triceratops");
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(scene);

        //Logo
        URL url = getClass().getResource("img/logo_simple.png");
        stage.getIcons().add(new Image(String.valueOf(url)));

        //stage.getIcons().add(new Image("data:image/png;base64,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"));

        //Taille min fenêtre
        stage.setMinWidth(750);
        stage.setMinHeight(500);
        stage.show();
    }


    public static void main(String[] args) {

        /*

        // TERRAIN DE JEU POUR COMPRENDRE LA PERSISTANCE (SERIALIZATION/DESERIALIZATION)
        ArrayList<Produit> produitsTest = new ArrayList<>();
        Produit p1 = new Produit(20,"TEST1","produit1",10,20,"kg");
        Produit p2 = new Produit(20,"TEST2","produit2",5,10,"L");
        Produit p3 = new Produit(30,"TEST3","produit3",10,30,"Kg");
        Produit p4 = new Produit(30,"TEST4","produit4",60,80,"Kg");
        produitsTest.add(p1);produitsTest.add(p2);produitsTest.add(p3);produitsTest.add(p4);


        ArrayList<ChaineProduction> chainesTest = new ArrayList<>();
        ChaineProduction chaineProd1 = new ChaineProduction(p3);
        chaineProd1.getProduitIn().put(p1.getCode(),5);
        chaineProd1.getProduitIn().put(p2.getCode(),1);
        ChaineProduction chaineProd2 = new ChaineProduction(p4);
        chaineProd2.getProduitIn().put(p3.getCode(),2);
        chaineProd2.getProduitIn().put(p1.getCode(),1);
        chaineProd2.getProduitIn().put(p2.getCode(),10);
        chainesTest.add(chaineProd1); chainesTest.add(chaineProd2);

        //      SERIALIZATION
        //addToFile(test,Produit.class,"produit.json");
        serializeToFile(produitsTest,"produit.json");
        serializeToFile(chainesTest, "chaine.json");
        //      DESERIALIZATION
        ArrayList<Produit> resultTestProduit = deserializeFromFile(Produit.class, "produit.json");
        ArrayList<ChaineProduction> resultTestChaine = deserializeFromFile(ChaineProduction.class, "chaine.json");
        System.out.println(resultTestChaine);

        */

        launch();
    }

}
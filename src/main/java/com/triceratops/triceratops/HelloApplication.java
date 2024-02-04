package com.triceratops.triceratops;

import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.Produit;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static com.triceratops.triceratops.persistance.InterfacePersistance.*;

public class HelloApplication extends Application {
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
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Triceratops");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(450);
        stage.show();
    }

    public static void main(String[] args) {


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


        launch();
    }
}
package com.triceratops.triceratops.controllers;

import com.triceratops.triceratops.HelloApplication;
import com.triceratops.triceratops.HelloController;
import com.triceratops.triceratops.persistance.FileException;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.triceratops.triceratops.modele.DataSet.extractDataset;

public class FileLoader {

    @FXML
    private AnchorPane fileLoader;

    private double xOffset, yOffset;
    private Cursor cursorEvent = Cursor.DEFAULT;

    @FXML
    private HBox windowHeader;

    @FXML
    private MFXFontIcon minimizeIcon,
            closeIcon;

    @FXML
    private MFXButton startBtn;


    private Stage stage;

    @FXML
    private VBox produit,
            chaine,
            prix;


    String fichierProduit,
            fichierPrix,
            fichierChaine;

    /**
     * Construsteur du chargement de fichier
     * @param stage fenetre de chargement
     */
    public FileLoader(Stage stage){
        this.stage = stage;
    }

    /**
     * Initialise la fenetre de chargement de fichier
     */
    public void initialize() {
         /*
            WindowsHeader
         */
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            // Création d'une transition de mise à l'échelle vers zéro
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.4), fileLoader);
            scaleTransition.setToX(0);
            scaleTransition.setToY(0);

            // Création d'une transition de fondu
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), fileLoader);
            fadeTransition.setToValue(0); // Fondu vers 0 (invisible)

            // Définition de l'action à effectuer à la fin de la transition
            fadeTransition.setOnFinished(finishedEvent -> {
                // Minimiser la fenêtre une fois que la transition est terminée
                Platform.exit();
            });

            // Démarrer les transitions
            scaleTransition.play();
            fadeTransition.play();
        });
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) fileLoader.getScene().getWindow()).setIconified(true));

        //Move windows
        windowHeader.setOnMousePressed(event -> {
            if(Cursor.DEFAULT.equals(cursorEvent)){
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        windowHeader.setOnMouseDragged(event -> {
            if(Cursor.DEFAULT.equals(cursorEvent)){
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        startBtn.setVisible(false);
    }

    /**
     * Permet de charger les différents fichiers à partir d'un evenement
     * @param actionEvent evenement lançant le chargement
     * @throws IOException
     */
    public void start(ActionEvent actionEvent) throws IOException {

        // Load File ....
        try {
            extractDataset(fichierProduit,fichierPrix,fichierChaine);
        } catch (FileException e) {
            throw new RuntimeException(e);
        }
        stage.close();

        /*
        *
        * Load App
        *
        */
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/root.fxml"));
        fxmlLoader.setControllerFactory(c -> new HelloController(stage));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("Triceratops");

        stage.setScene(scene);

        //Taille min fenêtre
        stage.setMinWidth(750);
        stage.setMinHeight(500);

        stage.show();
    }

    /**
     * Charge le fichier contenant la liste des produits
     * @param mouseEvent
     */
    public void openProduit(MouseEvent mouseEvent) {
        fichierProduit = openFile("Produit");
        reloadStartbtn();
        if(fichierProduit!=null)
            produit.setStyle("-fx-border-color: #ecba14;");
    }

    /**
     * Charge le fichier contenant la liste des chaines de productions
     * @param mouseEvent
     */
    public void openChaine(MouseEvent mouseEvent) {
        fichierChaine = openFile("Chaine");
        reloadStartbtn();
        if(fichierProduit!=null)
            chaine.setStyle("-fx-border-color: #ecba14;");
    }

    /**
     * Charge le fichier contenant la liste des prix
     * @param mouseEvent
     */
    public void openPrix(MouseEvent mouseEvent) {
        fichierPrix = openFile("Prix");
        reloadStartbtn();
        if(fichierProduit!=null)
            prix.setStyle("-fx-border-color: #ecba14;");
    }

    /**
     * Permet de selectionner un fichier dans son répertoire
     * @param name type de fichier à choisir
     * @return path du fichier choisi
     */
    private String openFile(String name){
        // Obtenez le répertoire parent du fichier JAR
        String currentDir = System.getProperty("user.dir");
        // Créer un objet File pour le répertoire parent
        File initialDirectory = new File(currentDir);

        // Créer une instance de FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.setTitle("Choisir une liste de "+name);

        // Ajouter un filtre pour les fichiers JSON
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers JSON (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Afficher la fenêtre de dialogue et attendre que l'utilisateur sélectionne un fichier
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Si un fichier est sélectionné, vous pouvez faire ce que vous voulez avec, par exemple l'imprimer
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            System.out.println("Fichier sélectionné : " + path);

            return path;

        }

        return null;
    }

    /**
     * Si tout les fichiers ont été chargés correctement, le bouton de démarrage devient visible
     */
    private void reloadStartbtn(){
        if(!Objects.equals(fichierProduit, "") && fichierProduit != null &&
                !Objects.equals(fichierChaine, "") && fichierChaine != null &&
                !Objects.equals(fichierPrix, "") && fichierPrix != null)
            startBtn.setVisible(true);
    }

}

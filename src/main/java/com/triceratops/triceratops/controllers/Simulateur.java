package com.triceratops.triceratops.controllers;

import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.Prix;
import com.triceratops.triceratops.modele.Produit;
import com.triceratops.triceratops.utils.NumberTextField;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import static com.triceratops.triceratops.modele.DataSet.*;
import static com.triceratops.triceratops.persistance.InterfacePersistance.deserializeFromFile;
import static com.triceratops.triceratops.persistance.InterfacePersistance.deserializeWithKeyFromFile;

public class Simulateur implements Initializable {
    public VBox tableSimu;
    public ScrollPane scroll;
    public MFXButton valid;

    private TableView<Produit> produitsInTable,
            produitsOutTable;

    private double margeTotal = 0,
            margeOut = 0;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Animation btn
    private boolean btnHover = false;

    /**
     * Permet d'initialiser la page du simulateur à partir d'un url et d'un ressourceBundle
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableSimu.setSpacing(20);
        setupTable();

        valid.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Animation lorsque survolé
                Timeline timeline = new Timeline();
                KeyValue widthValue = new KeyValue(valid.prefWidthProperty(), 200);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(150), widthValue);
                timeline.getKeyFrames().add(keyFrame);

                valid.setText("Sauvegarder  ");
                valid.setAlignment(Pos.CENTER_RIGHT);

                timeline.play();
            } else {
                // Animation lorsque non survolé
                Timeline timeline = new Timeline();
                KeyValue widthValue = new KeyValue(valid.prefWidthProperty(), 60);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(150), widthValue);
                timeline.getKeyFrames().add(keyFrame);
                timeline.play();

                valid.setText("");
            }
        });

    }

    /**
     * Permet de créer un tableau avec les données correspondant aux différentes chaines de production
     */

    private void setupTable() {
        /*
         *
         * Récupération Produits & Chaine de prod
         *
         */

        HashMap<String, Produit> produits = getProduits();
        ArrayList<ChaineProduction> chaineProductions = getChaines();
        ArrayList<Prix> prixArrayList = getPrix();

        /*
         *
         * Config Tableau
         *
         */

        for (ChaineProduction chaine : chaineProductions) {
            // Création d'un HBox pour chaque chaine de production
            VBox chaineVBox = new VBox();
            chaineVBox.setSpacing(20);
            chaineVBox.setAlignment(Pos.CENTER);

            // Ajout du nom de la chaine de production au HBox
            Text chaineNom = new Text(chaine.getNom());
            chaineNom.getStyleClass().add("titre");
            NumberTextField marge = new NumberTextField();
            marge.textProperty().setValue("Production");

            chaineVBox.getChildren().addAll(chaineNom, marge);

            HBox tableaux = new HBox();
            tableaux.setSpacing(30);

            // Création et configuration du TableView pour les produits IN
            Map<String, Integer> produitsInMap = chaine.getProduitIn();
            produitsInTable = createProduitsTableView(produitsInMap, produits, prixArrayList, chaine, 0, TypeTableau.IN);

            // Création et configuration du TableView pour les produits OUT
            Map<String, Integer> produitsOutMap = chaine.getProduitOut();
            produitsOutTable = createProduitsTableView(produitsOutMap, produits, prixArrayList, chaine,0, TypeTableau.OUT);
            margeOut = 0;

            // Ajout de la table des produits IN & OUT au HBox
            tableaux.getChildren().addAll(produitsInTable,produitsOutTable);

            marge.textProperty().addListener(((observable, oldValue, newValue) -> {

                if(newValue != null){

                    margeTotal -= margeOut;

                    if(newValue.equals(""))
                        newValue = "0";

                    int variance =  Integer.parseInt(newValue);

                    if(variance < 0)
                        variance = 0;

                    tableaux.getChildren().clear();
                    produitsInTable = createProduitsTableView(produitsInMap, produits, prixArrayList, chaine, variance, TypeTableau.IN);

                    //Calcul de la marge
                    margeOut = getMarge(prixArrayList,chaine,variance);
                    margeTotal += margeOut;

                    produitsOutTable = createProduitsTableView(produitsOutMap, produits, prixArrayList, chaine, variance, TypeTableau.OUT);
                    tableaux.getChildren().addAll(produitsInTable,produitsOutTable);
                }



                System.out.println("textfield changed from " + oldValue + " to " + newValue);

                System.out.println("margeTotal : " + margeTotal);
            }));



            //Ajout des tableaux a HBox
            chaineVBox.getChildren().add(tableaux);

            // Ajout du VBox à la VBox principale
            tableSimu.getChildren().add(chaineVBox);

            /*
            tableaux.getChildren().remove(produitsInTable);

            produitsInTable = createProduitsTableView(produitsInMap, produits, prixArrayList, chaine, TypeTableau.OUT);

            tableaux.getChildren().add(produitsInTable);

            produitsInTable.refresh();
             */
        }
    }

    public void valider(ActionEvent actionEvent) {
        // Valider simu @Delphine
    }

    private enum TypeTableau{IN, OUT}

    private TableView<Produit> createProduitsTableView(Map<String, Integer> produitsMap, HashMap<String, Produit> produits,
                                                       List<Prix> prixArrayList, ChaineProduction chaine, double variation,
                                                       TypeTableau typeTableau) {
        TableView<Produit> produitsTable = new TableView<>();
        produitsTable.setEditable(false);

        TableColumn<Produit, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Produit, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Produit, String> quantiteCol = new TableColumn<>("Quantité");

        TableColumn<Produit, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        //Produit iN && OUT
        TableColumn<Produit, String> variationCol = new TableColumn<>("Variation");


        // Utiliser une cellule personnalisée pour contrôler la couleur du texte
        /*variationCol.setCellFactory(column -> new TableCell<Produit, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setTextFill(javafx.scene.paint.Color.BLACK);  // Couleur par défaut si la cellule est vide
                } else {
                    setText(item);
                    double variationValue = Double.parseDouble(item.replace("- ", ""));
                    if (variationValue > 0) {
                        setTextFill(javafx.scene.paint.Color.GREEN); // Mettre en vert si la valeur est supérieure à la valeur spécifique
                    } else if (typeTableau == typeTableau.IN && variationValue < 0) {
                        setTextFill(javafx.scene.paint.Color.RED); // Mettre en rouge si la valeur est inférieure à la valeur spécifique
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK); // Sinon, utiliser la couleur par défaut
                    }
                }
            }
        });*/

        // Produit IN
        TableColumn<Produit, String> pAchatCol = new TableColumn<>("Prix d'achat");

        // Produit OUT
        TableColumn<Produit, String> pVenteCol = new TableColumn<>("Prix de vente");
        TableColumn<Produit, String> margeCol = new TableColumn<>("Marge");


        TableColumn<Produit, String> couvertureCommandeCol = new TableColumn<>("% Commande");

        if(typeTableau==TypeTableau.IN){
            produitsTable.setMaxWidth(445);


            quantiteCol.setCellValueFactory(cellData -> new SimpleStringProperty(chaine.getProduitIn().get(cellData.getValue().getCode()).toString()));
            variationCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                        "- "+String.valueOf(chaine.getProduitIn().get(
                                cellData.getValue().getCode()
                        )*variation)
                )
            );

            variationCol.setCellFactory(column -> new TableCell<Produit, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setTextFill(Color.BLACK); // Couleur par défaut si la cellule est vide
                    } else {
                        double value = chaine.getProduitIn().get(getTableView().getItems().get(getIndex()).getCode()) * variation;
                        double somme = getTableView().getItems().get(getIndex()).getQuantite() - value;
                        setText("- " + String.valueOf(value));
                        if (somme < 0) {
                            setTextFill(Color.RED); // Mettre en rouge si la somme est négative
                        } else {
                            setTextFill(Color.BLACK); // Sinon, utiliser la couleur par défaut
                        }
                    }
                }
            });

            pAchatCol.setCellValueFactory(cellData -> {
                double pAchat = getPrixProduit(cellData.getValue().getCode(), prixArrayList).getpAchat();
                return new SimpleStringProperty(pAchat >= 0 ? Double.toString(pAchat) : "NA");
            });

        }else{
            produitsTable.setMaxWidth(540);

            variationCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                        "+ "+String.valueOf(chaine.getProduitOut().get(
                                cellData.getValue().getCode()
                        )*variation)
                    )
            );

            variationCol.setCellFactory(column -> new TableCell<Produit, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setTextFill(Color.BLACK); // Couleur par défaut si la cellule est vide
                    } else {
                        double value = chaine.getProduitOut().get(getTableView().getItems().get(getIndex()).getCode()) * variation;
                        setText("+ " + String.valueOf(value));
                        if (value > 0) {
                            setTextFill(Color.GREEN); // Mettre en vert si la valeur est positive
                        } else {
                            setTextFill(Color.BLACK); // Sinon, utiliser la couleur par défaut
                        }
                    }
                }
            });

            pVenteCol.setCellValueFactory(cellData -> {
                double pVente = getPrixProduit(cellData.getValue().getCode(), prixArrayList).getpVente();
                return new SimpleStringProperty(pVente >= 0 ? Double.toString(pVente) : "NA");
            });

            margeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    String.valueOf(df.format(getMarge(prixArrayList,chaine, (int) variation)))
            ));

            couvertureCommandeCol.setCellValueFactory(cellData -> {
                double stock, prod, qteCommandé;

                stock = cellData.getValue().getQuantite();

                prod = chaine.getProduitOut().get(cellData.getValue().getCode())*variation;

                qteCommandé = getPrixProduit(cellData.getValue().getCode(), prixArrayList).getQuantiteCommande();

                System.out.println("stock : "+stock+" prod : "+prod+" qteCommandé : "+qteCommandé);

                return new SimpleStringProperty(qteCommandé > 0 ? (df.format(((stock + prod) / qteCommandé) * 100) + "%"): "NA" );
            });

            couvertureCommandeCol.setCellFactory(column -> new TableCell<Produit, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setTextFill(Color.BLACK); // Couleur par défaut si la cellule est vide
                    } else {
                        setText(item);
                        if (!item.equals("NA") && Double.parseDouble(item.replace("%","").replace(",",".")) < 100) {
                            setTextFill(Color.RED); // Mettre en rouge si la valeur est "NA"
                        } else {
                            setTextFill(Color.GREEN); // Utiliser la couleur par défaut pour les autres valeurs
                        }
                    }
                }
            });
        }


        /*TableColumn<Produit, String> quantiteProdCol = new TableColumn<>("Quantité de production");
        quantiteProdCol.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(getQuantiteProductionProduit(cellData.getValue().getCode(), chaine))));*/


        for (Map.Entry<String, Integer> entry : produitsMap.entrySet()) {
            Produit produit = produits.get(entry.getKey());
            /*
             * Debug
             */
//            System.out.println(produit);
//            System.out.println("qte : "+produit.getQuantite());
            if (produit != null) {
                produitsTable.getItems().add(produit);
            }
        }

        if(typeTableau==TypeTableau.IN){
            produitsTable.getColumns().addAll(codeCol, nomCol, quantiteCol, stockCol, variationCol, pAchatCol);
        }else{
            produitsTable.getColumns().addAll(codeCol, nomCol, stockCol, variationCol, pVenteCol, margeCol, couvertureCommandeCol);
        }

        produitsTable.setMaxHeight(200);

        return produitsTable;
    }


    // Méthode pour récupérer le prix d'un produit à partir de la liste des prix
    private Prix getPrixProduit(String codeProduit, List<Prix> prixList) {
        for (Prix prix : prixList) {
            if (prix.getCode().equals(codeProduit)) {
                return prix;
            }
        }
        return null; // Si aucun prix n'est trouvé pour le produit
    }

    private double getMarge(List<Prix> prixArrayList,
                           ChaineProduction chaine, int variation){
        double margeIn = 0,
                margeOut = 0;
        for (Map.Entry<String, Integer> entry : chaine.getProduitIn().entrySet())  {
            double prixAchat = getPrixProduit(entry.getKey(), prixArrayList).getpAchat();
            if(prixAchat > 0)
                margeIn += prixAchat  * entry.getValue() * variation;
        }

        //System.out.println(margeIn);

        for (Map.Entry<String, Integer> entry : chaine.getProduitOut().entrySet()){
            margeOut += (getPrixProduit(entry.getKey(), prixArrayList).getpVente() * variation);
        }

        //System.out.println(margeOut);

        return margeOut-margeIn;
    }

    // Méthode pour récupérer la quantité de production d'un produit à partir de la chaîne de production
    private int getQuantiteProductionProduit(String codeProduit, ChaineProduction chaine) {
        return chaine.getProduitOut().getOrDefault(codeProduit, 0);
    }



}

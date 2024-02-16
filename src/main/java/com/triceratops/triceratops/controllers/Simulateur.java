package com.triceratops.triceratops.controllers;

import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.Prix;
import com.triceratops.triceratops.modele.Produit;
import com.triceratops.triceratops.utils.NumberTextField;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

import static com.triceratops.triceratops.modele.DataSet.*;
import static com.triceratops.triceratops.persistance.InterfacePersistance.deserializeFromFile;
import static com.triceratops.triceratops.persistance.InterfacePersistance.deserializeWithKeyFromFile;

public class Simulateur implements Initializable {
    public VBox tableSimu;

    private TableView<Produit> produitsInTable,
            produitsOutTable;

    private double margeTotal = 0,
            margeOut;

    /**
     * Permet d'initialiser la page du simulateur à partir d'un url et d'un ressourceBundle
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableSimu.setSpacing(40);
        setupTable();
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
            NumberTextField marge = new NumberTextField();

            chaineVBox.getChildren().addAll(chaineNom, marge);

            HBox tableaux = new HBox();
            tableaux.setSpacing(40);

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
                    double margeProduitOUT = getMarge(prixArrayList,chaine,variance);

                    margeOut = margeProduitOUT;
                    margeTotal += margeProduitOUT;

                    produitsOutTable = createProduitsTableView(produitsOutMap, produits, prixArrayList, chaine, margeProduitOUT, TypeTableau.OUT);
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

        // Produit IN
        TableColumn<Produit, String> varianceCol = new TableColumn<>("Variation");
        TableColumn<Produit, String> pAchatCol = new TableColumn<>("Prix d'achat");

        // Produit OUT
        TableColumn<Produit, String> pVenteCol = new TableColumn<>("Prix de vente");
        TableColumn<Produit, String> margeCol = new TableColumn<>("Marge");

        if(typeTableau==TypeTableau.IN){
            quantiteCol.setCellValueFactory(cellData -> new SimpleStringProperty(chaine.getProduitIn().get(cellData.getValue().getCode()).toString()));
            varianceCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    String.valueOf(
                            -chaine.getProduitIn().get(
                                    cellData.getValue().getCode()
                            )*variation
                    )
            ));
            pAchatCol.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(getPrixProduit(cellData.getValue().getCode(), prixArrayList).getpAchat())));
        }else{
            pVenteCol.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(getPrixProduit(cellData.getValue().getCode(), prixArrayList).getpVente())));
            margeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    String.valueOf(variation)
            ));
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
            produitsTable.getColumns().addAll(codeCol, nomCol, quantiteCol, stockCol, varianceCol, pAchatCol);
        }else{
            produitsTable.getColumns().addAll(codeCol, nomCol, stockCol, pVenteCol, margeCol);
        }

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
                           ChaineProduction chaine, int variance){
        double margeIn = 0,
                margeOut = 0;
        for (Map.Entry<String, Integer> entry : chaine.getProduitIn().entrySet())  {
            double prixAchat = getPrixProduit(entry.getKey(), prixArrayList).getpAchat();
            if(prixAchat > 0)
                margeIn += prixAchat  * entry.getValue() * variance;
        }

        System.out.println(margeIn);

        for (Map.Entry<String, Integer> entry : chaine.getProduitOut().entrySet()){
            margeOut += (getPrixProduit(entry.getKey(), prixArrayList).getpVente() * variance);
        }

        System.out.println(margeOut);

        return margeOut-margeIn;
    }

    // Méthode pour récupérer la quantité de production d'un produit à partir de la chaîne de production
    private int getQuantiteProductionProduit(String codeProduit, ChaineProduction chaine) {
        return chaine.getProduitOut().getOrDefault(codeProduit, 0);
    }

}

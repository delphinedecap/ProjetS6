package com.triceratops.triceratops.controllers;

import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.Prix;
import com.triceratops.triceratops.modele.Produit;
import com.triceratops.triceratops.utils.NumberTextField;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static com.triceratops.triceratops.controllers.utils.PdfCreator.pdfSimulation;
import static com.triceratops.triceratops.controllers.utils.PdfCreator.pdfResultat;
import static com.triceratops.triceratops.controllers.utils.PlanProduction.*;
import static com.triceratops.triceratops.modele.DataSet.*;

public class Simulateur implements Initializable {
    public VBox tableSimu;
    public ScrollPane scroll;
    public MFXButton valid;
    private TableView<Produit> produitsInTable;
    private TableView<Produit> produitsOutTable;
    private HashMap<ChaineProduction, Integer> productions = new HashMap();
    private HashMap<String, Produit> produits = new HashMap<>();
    private ArrayList<Prix> prix = new ArrayList<>();
    private ArrayList<ChaineProduction> chaineProductions = new ArrayList<>();
    private enum TypeTableau{IN, OUT}
    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Permet d'initialiser la page du simulateur à partir d'un url et d'un ressourceBundle
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.produits = getProduits();
        this.chaineProductions = getChaines();
        this.prix = getPrix();

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

        Label tempsGlobal = new Label("Temps de production estimé : ");

        for (ChaineProduction chaine : this.chaineProductions) {
            // Création d'un HBox pour chaque chaine de production
            VBox chaineVBox = new VBox();
            chaineVBox.setSpacing(20);
            chaineVBox.setAlignment(Pos.CENTER);

            // Ajout du nom de la chaine de production au HBox
            Text chaineNom = new Text(chaine.getNom());
            chaineNom.getStyleClass().add("titre");
            NumberTextField marge = new NumberTextField();
            marge.textProperty().setValue("Production");

            Label resultLabel = new Label("Temps d'utilisation de la chaine : ");

            marge.textProperty().addListener(((observable, oldValue, newValue) -> {

                if(newValue != null) {

                    if (newValue.equals(""))
                        newValue = "0";

                    int variance = Integer.parseInt(newValue);
                    resultLabel.setText("Temps d'utilisation de la chaine : " + variance * chaine.getDuree() + " heures");

                }
            }));


            marge.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (estPossible(this.productions)) {
                    valid.setDisable(false);
                } else {
                    valid.setDisable(true);
                }
            });

            chaineVBox.getChildren().addAll(chaineNom, marge);
            chaineVBox.getChildren().add(resultLabel);

            HBox tableaux = new HBox();
            tableaux.setSpacing(30);

            // Création et configuration du TableView pour les produits IN
            Map<String, Integer> produitsInMap = chaine.getProduitIn();
            produitsInTable = createProduitsTableView(produitsInMap, this.produits, this.prix, chaine, 0, TypeTableau.IN);

            // Création et configuration du TableView pour les produits OUT
            Map<String, Integer> produitsOutMap = chaine.getProduitOut();
            produitsOutTable = createProduitsTableView(produitsOutMap, this.produits, this.prix, chaine,0, TypeTableau.OUT);

            // Ajout de la table des produits IN & OUT au HBox
            tableaux.getChildren().addAll(produitsInTable,produitsOutTable);

            marge.textProperty().addListener(((observable, oldValue, newValue) -> {

                if(newValue != null){

                    if(newValue.equals(""))
                        newValue = "0";

                    int variance =  Integer.parseInt(newValue);

                    if(variance < 0)
                        variance = 0;

                    this.productions.put(chaine, variance);

                    tableaux.getChildren().clear();
                    produitsInTable = createProduitsTableView(produitsInMap, produits, this.prix, chaine, variance, TypeTableau.IN);

                    produitsOutTable = createProduitsTableView(produitsOutMap, produits, this.prix, chaine, variance, TypeTableau.OUT);
                    tableaux.getChildren().addAll(produitsInTable,produitsOutTable);

                    int temps = getTempsProduction(this.productions);
                    System.out.println(temps);
                    if (temps == -1 && estPossible(this.productions)){
                        tempsGlobal.setText("Temps de production estimé supérieur à 60 heures ");
                    } else {
                        if (!estPossible(this.productions)) {
                            tempsGlobal.setText("La production n'est pas réalisable avec les stocks actuels");
                        } else {
                            tempsGlobal.setText("Temps de production estimé : " + temps + " heures ");
                        }
                    }
                }

            }));

            //Ajout des tableaux a HBox
            chaineVBox.getChildren().add(tableaux);

            chaineVBox.getChildren().add(tempsGlobal);

            // Ajout du VBox à la VBox principale
            tableSimu.getChildren().add(chaineVBox);

        }
    }

    /**
     * Création des pdf de résultat et de visualisation temporelle
     * @param actionEvent clic sur le bouton permettant de sauvegarder les productions
     */
    public void valider(ActionEvent actionEvent) {
        pdfSimulation(simulation(this.productions), getTempsProduction(this.productions));
        pdfResultat(this.productions);
    }

    /**
     * Creation de la table permettant de representer les differents produits utilisés ou produits par une chaine
     * @param produitsMap liste des produits
     * @param produits liste des produits
     * @param prixArrayList liste des prix
     * @param chaine chaine de production concernée
     * @param variation nombre de production demandée
     * @param typeTableau produitsIn ou produitsOut
     * @return tableau des produits
     */
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

        for (Map.Entry<String, Integer> entry : produitsMap.entrySet()) {
            Produit produit = produits.get(entry.getKey());
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

    /**
     * Retourne le prix d'un produit
     * @param codeProduit code du produit
     * @param prixList liste des prix
     * @return prix du produit
     */
    private static Prix getPrixProduit(String codeProduit, List<Prix> prixList) {
        for (Prix prix : prixList) {
            if (prix.getCode().equals(codeProduit)) {
                return prix;
            }
        }
        return null; // Si aucun prix n'est trouvé pour le produit
    }

    /**
     * Renvoie la marge réalisé par une chaine de production
     * @param prixArrayList liste des prix
     * @param chaine chaine de production concernée
     * @param variation nombre de production demandée
     * @return marge totale
     */
    public static double getMarge(List<Prix> prixArrayList,
                                  ChaineProduction chaine, int variation){
        double margeIn = 0,
                margeOut = 0;
        for (Map.Entry<String, Integer> entry : chaine.getProduitIn().entrySet())  {
            double prixAchat = getPrixProduit(entry.getKey(), prixArrayList).getpAchat();
            if(prixAchat > 0)
                margeIn += prixAchat  * entry.getValue() * variation;
        }

        for (Map.Entry<String, Integer> entry : chaine.getProduitOut().entrySet()){
            margeOut += (getPrixProduit(entry.getKey(), prixArrayList).getpVente() * variation);
        }

        return margeOut-margeIn;
    }

}
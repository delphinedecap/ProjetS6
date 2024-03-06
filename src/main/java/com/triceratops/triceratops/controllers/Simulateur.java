package com.triceratops.triceratops.controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
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

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static com.triceratops.triceratops.modele.DataSet.*;

public class Simulateur implements Initializable {
    public VBox tableSimu;
    public ScrollPane scroll;
    public MFXButton valid;

    private TableView<Produit> produitsInTable,
            produitsOutTable;

    private double margeTotal = 0,
            margeOut = 0;

    private HashMap<ChaineProduction, Integer> productions = new HashMap();

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

                    this.productions.put(chaine, variance);

                    tableaux.getChildren().clear();
                    produitsInTable = createProduitsTableView(produitsInMap, produits, prixArrayList, chaine, variance, TypeTableau.IN);

                    //Calcul de la marge
                    margeOut = getMarge(prixArrayList,chaine,variance);
                    margeTotal += margeOut;

                    produitsOutTable = createProduitsTableView(produitsOutMap, produits, prixArrayList, chaine, variance, TypeTableau.OUT);
                    tableaux.getChildren().addAll(produitsInTable,produitsOutTable);
                }

            }));



            //Ajout des tableaux a HBox
            chaineVBox.getChildren().add(tableaux);

            // Ajout du VBox à la VBox principale
            tableSimu.getChildren().add(chaineVBox);

        }
    }

    public void valider(ActionEvent actionEvent) {
        // Valider simu @Delphine
        HashMap<String, Produit> produits = getProduits();
        ArrayList<Prix> prix = getPrix();
        //created PDF document instance
        Document doc = new Document();
        try
        {
            //generate a PDF at the specified location
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("C:\\Users\\Public\\Downloads\\Results.pdf"));
            System.out.println("PDF created.");
            //opens the PDF
            doc.open();
            //adds paragraph to the PDF file

            Font bold16 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Font bold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font bold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            doc.add(new Paragraph("Résultat prévisionnel de production : ", bold16));
            doc.add(new Paragraph(" "));

            //
            for (Map.Entry<ChaineProduction, Integer> mapEntry : productions.entrySet()) {
                ChaineProduction chaine = mapEntry.getKey();
                Integer production = mapEntry.getValue();

                doc.add(new Paragraph(chaine.getNom() , bold14));
                doc.add(new Paragraph("Production demandée : " + production, bold14));
                doc.add(new Paragraph("Durée de la production unitaire : " + production*chaine.getDuree() +"min", bold14));
                doc.add(new Paragraph("Produits entrants : " , bold12));

                PdfPTable tableEntrant = new PdfPTable(6);

                PdfPCell cell1 = new PdfPCell();
                Paragraph codeTexte = new Paragraph("Code ",bold12);
                cell1.addElement(codeTexte);
                cell1.setRowspan(3);
                cell1.setBorder(0);
                tableEntrant.addCell(cell1);

                PdfPCell cell2 = new PdfPCell();
                Paragraph nomTexte = new Paragraph("Nom ",bold12);
                cell2.addElement(nomTexte);
                cell2.setRowspan(3);
                cell2.setBorder(0);
                tableEntrant.addCell(cell2);

                PdfPCell cell3 = new PdfPCell();
                Paragraph quantiteTexte = new Paragraph("Quantité ",bold12);
                cell3.addElement(quantiteTexte);
                cell3.setRowspan(3);
                cell3.setBorder(0);
                tableEntrant.addCell(cell3);

                PdfPCell cell4 = new PdfPCell();
                Paragraph stockTexte = new Paragraph("Stock ",bold12);
                cell4.addElement(stockTexte);
                cell4.setRowspan(3);
                cell4.setBorder(0);
                tableEntrant.addCell(cell4);

                PdfPCell cell5 = new PdfPCell();
                Paragraph variationTexte = new Paragraph("Variation ",bold12);
                cell5.addElement(variationTexte);
                cell5.setRowspan(3);
                cell5.setBorder(0);
                tableEntrant.addCell(cell5);

                PdfPCell cell6 = new PdfPCell();
                Paragraph prixTexte = new Paragraph("Prix d'achat ",bold12);
                cell6.addElement(prixTexte);
                cell6.setRowspan(3);
                cell6.setBorder(0);
                tableEntrant.addCell(cell6);

                doc.add(tableEntrant);

                Map<String, Integer> produitsIn = chaine.getProduitIn();

                PdfPTable ligne;
                for ( String code: produitsIn.keySet()) {
                    Produit p = produits.get(code);
                    ligne= new PdfPTable(6);

                    cell1 = new PdfPCell();
                    codeTexte = new Paragraph(p.getCode() , font);
                    cell1.addElement(codeTexte);
                    cell1.setRowspan(3);
                    cell1.setBorder(0);
                    ligne.addCell(cell1);

                    cell2 = new PdfPCell();
                    nomTexte = new Paragraph(p.getNom(), font);
                    cell2.addElement(nomTexte);
                    cell2.setRowspan(3);
                    cell2.setBorder(0);
                    ligne.addCell(cell2);

                    cell3 = new PdfPCell();
                    quantiteTexte = new Paragraph(String.valueOf(produitsIn.get(code)),font);
                    cell3.addElement(quantiteTexte);
                    cell3.setRowspan(3);
                    cell3.setBorder(0);
                    ligne.addCell(cell3);

                    cell4 = new PdfPCell();
                    stockTexte = new Paragraph(String.valueOf(p.getQuantite()),font);
                    cell4.addElement(stockTexte);
                    cell4.setRowspan(3);
                    cell4.setBorder(0);
                    ligne.addCell(cell4);

                    cell5 = new PdfPCell();
                    double variation = produitsIn.get(code) * production * -1.0;
                    variationTexte = new Paragraph(String.valueOf(variation),font);
                    cell5.addElement(variationTexte);
                    cell5.setRowspan(3);
                    cell5.setBorder(0);
                    ligne.addCell(cell5);

                    Double prixAchat = 0.0;
                    for (Prix prix1 : prix){
                        if (Objects.equals(prix1.getCode(), code)){
                            prixAchat = prix1.getpAchat();
                        }
                    }
                    String prixAchatString;
                    if (prixAchat >0){
                        prixAchatString = String.valueOf(prixAchat);
                    } else {
                        prixAchatString = "NA";
                    }

                    cell6 = new PdfPCell();
                    prixTexte = new Paragraph(prixAchatString,font);
                    cell6.addElement(prixTexte);
                    cell6.setRowspan(3);
                    cell6.setBorder(0);
                    ligne.addCell(cell6);

                    doc.add(ligne);
                }


                //Tableau produits sortants
                doc.add(new Paragraph("Produits sortants : " , bold12));

                PdfPTable tableSortants = new PdfPTable(7);

                PdfPCell cells1 = new PdfPCell();
                Paragraph codeSortantTexte = new Paragraph("Code ",bold12);
                cells1.addElement(codeSortantTexte);
                cells1.setRowspan(3);
                cells1.setBorder(0);
                tableSortants.addCell(cells1);

                PdfPCell cells2 = new PdfPCell();
                Paragraph nomTexteSortant = new Paragraph("Nom ",bold12);
                cells2.addElement(nomTexteSortant);
                cells2.setRowspan(3);
                cells2.setBorder(0);
                tableSortants.addCell(cells2);


                PdfPCell cells3 = new PdfPCell();
                Paragraph stockTexteSortant = new Paragraph("Stock ",bold12);
                cells3.addElement(stockTexteSortant);
                cells3.setRowspan(3);
                cells3.setBorder(0);
                tableSortants.addCell(cells3);

                PdfPCell cells4 = new PdfPCell();
                Paragraph variationTextesortant = new Paragraph("Variation ",bold12);
                cells4.addElement(variationTextesortant);
                cells4.setRowspan(3);
                cells4.setBorder(0);
                tableSortants.addCell(cells4);

                PdfPCell cells5 = new PdfPCell();
                Paragraph prixTexteSortant = new Paragraph("Prix de vente ",bold12);
                cells5.addElement(prixTexteSortant);
                cells5.setRowspan(3);
                cells5.setBorder(0);
                tableSortants.addCell(cells5);

                PdfPCell cells6 = new PdfPCell();
                Paragraph margeTexte = new Paragraph("Marge  ",bold12);
                cells6.addElement(margeTexte);
                cells6.setRowspan(3);
                cells6.setBorder(0);
                tableSortants.addCell(cells6);

                PdfPCell cells7 = new PdfPCell();
                Paragraph commande = new Paragraph("Commande ",bold12);
                cells7.addElement(commande);
                cells7.setRowspan(3);
                cells7.setBorder(0);
                tableSortants.addCell(cells7);

                doc.add(tableSortants);

                Map<String, Integer> produitsOut = chaine.getProduitOut();

                for ( String code: produitsOut.keySet()) {
                    Produit p = produits.get(code);
                    ligne= new PdfPTable(7);

                    cell1 = new PdfPCell();
                    codeTexte = new Paragraph(p.getCode() , font);
                    cell1.addElement(codeTexte);
                    cell1.setRowspan(3);
                    cell1.setBorder(0);
                    ligne.addCell(cell1);

                    cell2 = new PdfPCell();
                    nomTexte = new Paragraph(p.getNom(), font);
                    cell2.addElement(nomTexte);
                    cell2.setRowspan(3);
                    cell2.setBorder(0);
                    ligne.addCell(cell2);

                    cell3 = new PdfPCell();
                    stockTexte = new Paragraph(String.valueOf(p.getQuantite()),font);
                    cell3.addElement(stockTexte);
                    cell3.setRowspan(3);
                    cell3.setBorder(0);
                    ligne.addCell(cell3);

                    cell4 = new PdfPCell();
                    double variation = produitsOut.get(code) * production ;
                    variationTexte = new Paragraph(String.valueOf(variation),font);
                    cell4.addElement(variationTexte);
                    cell4.setRowspan(3);
                    cell4.setBorder(0);
                    ligne.addCell(cell4);

                    Double prixVente = 0.0;
                    for (Prix prix1 : prix){
                        if (Objects.equals(prix1.getCode(), code)){
                            prixVente = prix1.getpVente();
                        }
                    }
                    String prixVenteString;
                    if (prixVente >0){
                        prixVenteString = String.valueOf(prixVente);
                    } else {
                        prixVenteString = "NA";
                    }

                    cell5 = new PdfPCell();
                    prixTexte = new Paragraph(prixVenteString,font);
                    cell5.addElement(prixTexte);
                    cell5.setRowspan(3);
                    cell5.setBorder(0);
                    ligne.addCell(cell5);

                    cell6 = new PdfPCell();
                    double marge = getMarge(prix,chaine,production);
                    margeTexte = new Paragraph(String.valueOf(marge), font);
                    cell6.addElement(margeTexte);
                    cell6.setRowspan(3);
                    cell6.setBorder(0);
                    ligne.addCell(cell6);

                    int quantiteCommande = 0;
                    for (Prix prix1 : prix){
                        if (Objects.equals(prix1.getCode(), code)){
                            quantiteCommande = prix1.getQuantiteCommande();
                        }
                    }
                    cells7 = new PdfPCell();
                    double commandes = (p.getQuantite() + variation) / quantiteCommande *100;
                    String StringCommande;
                    if (quantiteCommande ==0){
                        StringCommande = "NA";
                    } else {
                        StringCommande = String.valueOf(commandes)+"%";
                    }
                    commande = new Paragraph(StringCommande, font);
                    cells7.addElement(commande);
                    cells7.setRowspan(3);
                    cells7.setBorder(0);
                    ligne.addCell(cells7);

                    doc.add(ligne);
                }
                doc.add(new Paragraph(" "));
                doc.add(new Paragraph(" "));

            }

            //close the PDF file
            doc.close();
            //closes the writer
            writer.close();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

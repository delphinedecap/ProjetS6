package com.triceratops.triceratops.controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
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

            marge.hoverProperty().addListener((observable, oldValue, newValue) -> {
                        if (estPossible()) {
                            valid.setDisable(false);
                        } else {
                            valid.setDisable(true);
                        }
            });

            chaineVBox.getChildren().addAll(chaineNom, marge);

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
                }

            }));

            //Ajout des tableaux a HBox
            chaineVBox.getChildren().add(tableaux);

            // Ajout du VBox à la VBox principale
            tableSimu.getChildren().add(chaineVBox);

        }
    }

    /**
     * Permet de savoir si la production demandée est possible avec les stocks actuels
     * @return vraie si c'est possible, faux sinon
     */
    private boolean estPossible(){
        HashMap<String, Integer> produits = new HashMap<>();
        //Calcul de la variation de stock engendré par la production
        for (Map.Entry<ChaineProduction, Integer> mapEntry : productions.entrySet()) {
            ChaineProduction chaine = mapEntry.getKey();
            Integer production = mapEntry.getValue();
            Map<String, Integer> produitsIn = chaine.getProduitIn();
            //Calcul des produits utilisés par la chaine
            for ( String code: produitsIn.keySet()) {
                if (produits.containsKey(code)){
                    produits.put(code, produits.get(code)-produitsIn.get(code) * production );
                } else {
                    produits.put(code, produitsIn.get(code) * production * -1);
                }
            }

            Map<String, Integer> produitsOut = chaine.getProduitOut();
            //Calcul des produits produits par la chaine
            for ( String code: produitsOut.keySet()) {
                if (produits.containsKey(code)){
                    produits.put(code, produits.get(code)+ produitsOut.get(code) * production );
                } else {
                    produits.put(code,produitsOut.get(code) * production );
                }
            }
        }

        //Comparaison au stock actuel
        //Si un stock actuel + variation est négatif alors la production n'est pas possible
        for (Map.Entry<String, Integer> mapEntry : produits.entrySet()) {
            String code = mapEntry.getKey();
            Integer i = mapEntry.getValue();
            if (i+this.produits.get(code).getQuantite()<0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Création des pdf de résultat et de visualisation temporelle
     * @param actionEvent clic sur le bouton permettant de sauvegarder les productions
     */
    public void valider(ActionEvent actionEvent) {
        pdfSimulation();
        pdfResultat();
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


        /*TableColumn<Produit, String> quantiteProdCol = new TableColumn<>("Quantité de production");
        quantiteProdCol.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(getQuantiteProductionProduit(cellData.getValue().getCode(), chaine))));*/


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
    private Prix getPrixProduit(String codeProduit, List<Prix> prixList) {
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
    private double getMarge(List<Prix> prixArrayList,
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

    /**
     * Simule heure par heure l'avancement des chaines de production
     * @return chaine de production et la liste de leurs temps finaux d'execution
     */
    public HashMap<ChaineProduction, ArrayList<Integer>> simulation(){
        HashMap<String, Integer> stockSimule  = new HashMap<>();
        for (Produit p : this.produits.values()){
            stockSimule.put(p.getCode(), p.getQuantite());
        }
        HashMap<ChaineProduction, Integer> productionsEnCours = new HashMap<>();
        HashMap<ChaineProduction, ArrayList<Integer>> productionsFinies= new HashMap<>();
        HashMap<ChaineProduction, Integer> productionAFaire = (HashMap<ChaineProduction, Integer>) this.productions.clone();
        for (int i=0; i<60; i++){
            //gestion des chaines deja lancées
            HashMap<ChaineProduction, Integer> productionsEnCoursCopy = new HashMap<>(productionsEnCours);
            for (ChaineProduction c : productionsEnCoursCopy.keySet()) {
                if (productionsEnCours.get(c) == 1) {
                    productionsEnCours.remove(c);
                    if (productionsFinies.containsKey(c)) {
                        productionsFinies.get(c).add(i);
                    } else {
                        ArrayList<Integer> l = new ArrayList<>();
                        l.add(i);
                        productionsFinies.put(c, l);
                    }
                    //ajout des produits créés au stock
                    for (String p : c.getProduitOut().keySet()) {
                        if (stockSimule.containsKey(p)) {
                            int j = stockSimule.get(p) + c.getProduitOut().get(p);
                            stockSimule.replace(p, j);
                        } else {
                            stockSimule.put(p, c.getProduitOut().get(p));
                        }
                    }
                } else {
                    productionsEnCours.replace(c,productionsEnCours.get(c)-1);
                }
            }

            // lancement des nouvelles chaines possibles
            HashMap<ChaineProduction, Integer> productionAFaireCopy = new HashMap<>(productionAFaire);
            for (ChaineProduction c : productionAFaireCopy.keySet()) {
                if (c.estRealisable(stockSimule) && !productionsEnCours.containsKey(c)) {
                    productionsEnCours.put(c, c.getDuree());
                    productionAFaire.replace(c, productionAFaire.get(c) - 1);
                    if (productionAFaire.get(c) == 0) {
                        productionAFaire.remove(c);
                    }
                    //mise à jour des stocks
                    for (String p : c.getProduitIn().keySet()) {
                        int j = stockSimule.get(p) - c.getProduitIn().get(p);
                        stockSimule.replace(p, j);
                    }
                }
            }
        }

        if(productionAFaire.isEmpty() && productionsEnCours.isEmpty()){
            return productionsFinies;
        }
        return null;
    }

    /**
     * Edite le pdf correspondant à l'avancement heure par heure des chaines de productions
     * Le pdf sera sauvegardé dans C:\Users\Public\Downloads
     */
    public void pdfSimulation(){
        HashMap<ChaineProduction, ArrayList<Integer>> result = simulation();
        Document doc = new Document();
        try
        {
            //generate a PDF at the specified location
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("C:\\Users\\Public\\Downloads\\Simulation.pdf"));
            System.out.println("PDF created.");
            //opens the PDF
            doc.open();
            //adds paragraph to the PDF file

            Font bold16 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Font bold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font bold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            doc.add(new Paragraph("Temps fonctionnement : ", bold16));
            doc.add(new Paragraph(" "));

            if (result == null){
                doc.add(new Paragraph("La production demandée n'est pas réalisable en 60 heures", bold14));
            } else {
                for (ChaineProduction c : result.keySet()){
                    doc.add(new Paragraph(c.getNom() , bold14));
                    doc.add(new Paragraph("Durée de fonctionnement de la chaine : " + c.getDuree()*result.get(c).size() +"heures", bold12));
                    int duree = c.getDuree();
                    for (int i=0; i<result.get(c).size(); i++){
                        int tempsFin = result.get(c).get(i);
                        doc.add(new Paragraph("     Itération " + (i+1) + " : de heure " + (tempsFin-duree ) + " à heure " + tempsFin, bold12));
                    }
                    doc.add(new Paragraph(" "));
                }
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

    /**
     * Edite le pdf correspondant à la production demandée
     * Le pdf sera sauvegardé dans C:\Users\Public\Downloads
     */
    public void pdfResultat(){
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
                doc.add(new Paragraph("Durée de la production unitaire : " + production*chaine.getDuree() +"heures", bold14));
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
                    Produit p = this.produits.get(code);
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
                    for (Prix prix1 : this.prix){
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
                    Produit p = this.produits.get(code);
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
                    for (Prix prix1 : this.prix){
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
                    double marge = getMarge(this.prix,chaine,production);
                    margeTexte = new Paragraph(String.valueOf(marge), font);
                    cell6.addElement(margeTexte);
                    cell6.setRowspan(3);
                    cell6.setBorder(0);
                    ligne.addCell(cell6);

                    int quantiteCommande = 0;
                    for (Prix prix1 : this.prix){
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
}
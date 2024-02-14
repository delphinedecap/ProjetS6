package com.triceratops.triceratops.controllers;

import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.LigneSimu;
import com.triceratops.triceratops.modele.Produit;
import com.triceratops.triceratops.modele.StockProduit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.awt.*;
import java.net.URL;
import java.util.*;

import static com.triceratops.triceratops.persistance.InterfacePersistance.deserializeFromFile;

public class Simulateur implements Initializable {
    public TableView tableSimu;

    /**
     * Permet d'initialiser la page du simulateur à partir d'un url et d'un ressourceBundle
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //tableSimu.autosizeColumnsOnInitialization();
        setupTable();
        tableSimu.setEditable(true);
        //tableSimu.setFooterVisible(false);
    }

    /**
     * Permet de créer un tableau avec les données correspondant aux différentes chaines de production
     */
    private void setupTable() {
        TableColumn<LigneSimu, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("codeProduit"));
        codeColumn.setSortable(false);
        codeColumn.setMinWidth(100);

        TableColumn<LigneSimu, String> quantiteColumn = new TableColumn<>("Qte");
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("quantite"));
        quantiteColumn.setSortable(false);
        quantiteColumn.setMinWidth(60);


        TableColumn<LigneSimu, String> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("stockTotal"));
        stockColumn.setSortable(false);
        stockColumn.setMinWidth(60);



        TableColumn<LigneSimu, String> variationColumn = new TableColumn<>("Variation");
        variationColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("variation"));
        variationColumn.setSortable(false);
        variationColumn.setMinWidth(70);

        TableColumn<LigneSimu, String> productionColumn = new TableColumn<>("Production");
        productionColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("production"));
        productionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productionColumn.setSortable(false);
        productionColumn.setMinWidth(80);
        productionColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<LigneSimu, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<LigneSimu, String> event) {
                LigneSimu ligneSimu = event.getRowValue();
                if(!Objects.equals(ligneSimu.getProduction(), "")){
                    ligneSimu.setProduction(Integer.parseInt(event.getNewValue()));
                }
                event.getTableView().refresh();
                //event.getTableView().setItems(event.getTableView().getItems());
            }
        });

        /*MFXTableColumn<LigneSimu> productionColumn = new MFXTableColumn<>("Production", false);
        //productionColumn.setRowCellFactory(TextFieldTableCell.<String>forTableColumn());
        productionColumn.setRowCellFactory(ligneSimu -> {
            String str = ligneSimu.getProduction();

            MFXTableRowCell row = new MFXTableRowCell<>(null);
            if(str != ""){
                MFXTextField textField = new MFXTextField(str);
                Shape shape = new Rectangle(40,40);
                Text text = new Text();
                row.setGraphic(textField);
                row.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);


            }
            return row;
        });
        productionColumn.setAlignment(Pos.CENTER);*/


        TableColumn<LigneSimu, String> coutUnitColumn = new TableColumn<>("Cout unit");
        coutUnitColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("coutUnit"));
        coutUnitColumn.setSortable(false);
        coutUnitColumn.setMinWidth(65);


        TableColumn<LigneSimu, String> margeColumn = new TableColumn<>("Marge");
        margeColumn.setCellValueFactory(new PropertyValueFactory<LigneSimu,String>("marge"));
        margeColumn.setSortable(false);
        margeColumn.setMinWidth(60);


        // Ajouter les colonnes à TableView
        tableSimu.getColumns().addAll(codeColumn, quantiteColumn, stockColumn, variationColumn,
                productionColumn, coutUnitColumn, margeColumn);


        //tableSimu.getColumnResizePolicy();

        StockProduit stockProduit = new StockProduit(deserializeFromFile(Produit.class, "produit.json"));
        //System.out.println(stockProduit);
        ArrayList<ChaineProduction> stockChaineP = deserializeFromFile(ChaineProduction.class, "chaine.json");
        //ObservableList<Produit> observableList = FXCollections.observableArrayList(stockProduit.getStock());

        ArrayList<LigneSimu> data = new ArrayList<>();

        for (ChaineProduction chaineProduction:stockChaineP){

            HashMap<LigneSimu,Integer> mapProduitChaine = new HashMap<>();

            for (Map.Entry<String,Integer> e : chaineProduction.getProduitIn().entrySet()){
                Produit p = stockProduit.getProduit(e.getKey());
                if(p != null){
                    LigneSimu ligneSimu = new LigneSimu(p,e.getValue(),0f);
                    data.add(ligneSimu);
                    mapProduitChaine.put(ligneSimu,e.getValue());
                }else{
                    //Produit introuvable
                }
            }
            /*
            Produit pOut = stockProduit.getProduit(chaineProduction.getProduitOut().getCode());

            //new LigneSimu de chaine de prod
            data.add(new LigneSimu(pOut, mapProduitChaine,1, 0f));
            //data.add(new LigneSimu(chaineProduction, 1, 0f));
            data.add(new LigneSimu());

             */
        }

        // Somme / Dernière ligne
        data.add(new LigneSimu(data));


        /*ObservableList<LigneSimu> observableList = FXCollections.observableArrayList(data);

        tableSimu.setItems(observableList);*/
        tableSimu.setItems(FXCollections.observableArrayList(data));
    }

}

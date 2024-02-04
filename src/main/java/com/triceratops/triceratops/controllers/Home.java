package com.triceratops.triceratops.controllers;

import com.triceratops.triceratops.modele.Produit;
import com.triceratops.triceratops.modele.StockP;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class Home implements Initializable {

    @FXML
    private MFXTableView<Produit> paginated;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        paginated.autosizeColumnsOnInitialization();

        setupPaginated();

        paginated.setFooterVisible(false);

        /*When.onChanged(paginated.currentPageProperty())
                .then((oldValue, newValue) -> paginated.autosizeColumns())
                .listen();*/
    }


    private void setupPaginated() {
        MFXTableColumn<Produit> codeColumn = new MFXTableColumn<>("Code", false, Comparator.comparing(Produit::getCode));
        MFXTableColumn<Produit> nomColumn = new MFXTableColumn<>("Nom", false, Comparator.comparing(Produit::getNom));
        MFXTableColumn<Produit> quantiteColumn = new MFXTableColumn<>("Quantite", false, Comparator.comparing(Produit::getQuantite));

        codeColumn.setRowCellFactory(produit -> new MFXTableRowCell<>(Produit::getCode));
        nomColumn.setRowCellFactory(produit -> new MFXTableRowCell<>(Produit::getNom));
        quantiteColumn.setRowCellFactory(produit -> new MFXTableRowCell<>(Produit::getQuantite));

        codeColumn.setAlignment(Pos.CENTER);
        nomColumn.setAlignment(Pos.CENTER);
        quantiteColumn.setAlignment(Pos.CENTER);

        paginated.getTableColumns().addAll(codeColumn,nomColumn,quantiteColumn);
        paginated.getFilters().addAll(
                new StringFilter<>("Code", Produit::getCode),
                new StringFilter<>("Nom", Produit::getNom),
                new IntegerFilter<>("Quantite", Produit::getQuantite)
        );


        //Test
        StockP stockP = new StockP("stock");

        Produit p1 = new Produit(2,"P32426","produit de test 1",24,50,"kg");
        Produit p2 = new Produit(5,"P27624","produit de test 2",7,20,"kg");

        stockP.add(p1);
        stockP.add(p2);
        stockP.add(p1);
        stockP.add(p2);
        stockP.add(p1);
        stockP.add(p2);
        stockP.add(p1);
        stockP.add(p2);
        stockP.add(p1);
        stockP.add(p2);
        stockP.add(p1);
        stockP.add(p2);
        stockP.add(p1);
        stockP.add(p2);

        ObservableList<Produit> observableList = FXCollections.observableArrayList(stockP.getStock());

        paginated.setItems(observableList);
    }
}

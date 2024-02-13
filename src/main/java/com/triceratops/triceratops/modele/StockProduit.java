package com.triceratops.triceratops.modele;

import java.util.ArrayList;

public class StockProduit {

    private ArrayList<Produit> stock = new ArrayList<>();


    public StockProduit(ArrayList<Produit> stock) {
        this.stock = stock;
    }

    public ArrayList<Produit> getStock() {
        return stock;
    }

    public void add(Produit p){
        stock.add(p);
    }

    public Produit getProduit(String code){
        for (Produit p:stock){
            if (p.getCode().equals(code)){
                return p;
            }
        }

        return null;
    }

    public Produit remove(String code){
        for (Produit p:stock){
            if (p.getCode().equals(code)){
                stock.remove(p);
                return p;
            }
        }

        return null;
    }


    @Override
    public String toString() {
        StringBuilder t = new StringBuilder("Stock :");

        t.append("\n");

        for (Produit p:stock){
            t.append(" ------ ").append(p).append("\n");
        }

        return t.toString();
    }
}

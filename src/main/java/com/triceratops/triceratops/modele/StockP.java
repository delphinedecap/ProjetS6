package com.triceratops.triceratops.modele;

import java.util.ArrayList;

public class StockP {

    private ArrayList<Produit> stock = new ArrayList<>();
    private String nom;

    public StockP(String nom) {
        this.nom = nom;
    }

    public StockP(ArrayList<Produit> stock, String nom) {
        this.stock = stock;
        this.nom = nom;
    }

    public ArrayList<Produit> getStock() {
        return stock;
    }

    public void add(Produit p){
        stock.add(p);
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

    public String getNom() {
        return nom;
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

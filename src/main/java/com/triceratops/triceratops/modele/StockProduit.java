package com.triceratops.triceratops.modele;

import java.util.ArrayList;

public class StockProduit {

    private ArrayList<Produit> stock = new ArrayList<>();

    /**
     * Constructeur par defaut d'un stock de produits
     */
    public StockProduit(ArrayList<Produit> stock) {
        this.stock = stock;
    }

    /**
     *
     * @return liste de produits
     */
    public ArrayList<Produit> getStock() {
        return stock;
    }

    /**
     * Ajoute un produit dans le stock
     */
    public void add(Produit p){
        stock.add(p);
    }

    /**
     * Renvoie un produit à partir de son code si il est en stock
     * @param code code du produit
     * @return produit correspondant au code
     */
    public Produit getProduit(String code){
        for (Produit p:stock){
            if (p.getCode().equals(code)){
                return p;
            }
        }

        return null;
    }

    /**
     * Enleve un produit du stock à partir de son code
     * @param code code du produit
     * @return produit enlevé
     */
    public Produit remove(String code){
        for (Produit p:stock){
            if (p.getCode().equals(code)){
                stock.remove(p);
                return p;
            }
        }

        return null;
    }


    /**
     *
     * @return Texte correspondant au stock
     */
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

package com.triceratops.triceratops.modele;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LigneSimu {
    private String codeProduit;

    private Produit p;

    private HashMap<LigneSimu,Integer> mapProduitChaine;

    private ArrayList<LigneSimu> arraySomme;

    private int quantite, stock;

    private int variation, production;

    private float coutUnit;
    private float marge;

    private boolean espace, modeProd, modeSomme;

    public  LigneSimu(){
        espace = true;
    }

    /*
           Constructeur Produit
        */
    public  LigneSimu(Produit p, int quantite, float marge){
        this.codeProduit = p.getCode();
        //Stock de l'objet produit pour changer les autres
        this.p = p;

        //Qte nécéssaire
        this.quantite = quantite;
        //Qte dispo
        this.stock = p.getQuantite();

        this.coutUnit = p.getpAchat();
        this.marge = marge;
        this.production = -1;
    }

    /*
       Constructeur Chaine de production
    */
    public  LigneSimu(Produit p, HashMap<LigneSimu,Integer> map , int quantite, float marge){
        this(p, quantite, marge);
        this.mapProduitChaine = map;
        this.production = 0;
        this.modeProd = true;

        this.calculMarge();
    }


    public  LigneSimu( ArrayList<LigneSimu> data ){
        this.codeProduit = "Somme";
        this.modeSomme = true;
        this.marge = 0;

        data.remove(this);

        this.arraySomme = data;

        calculSomme();
    }


    public Produit getP() {
        return p;
    }

    public String getVariation() {
        if(espace || modeSomme){
            return "";
        }
        return variation+"";
    }

    public void setVariation(int variation) {
        this.variation = variation;
    }

    public void setProduction(int production) {
        //Vérification que c'est une chaine de prod
        if(modeProd && production >= 0){
            int variation = this.production-production;
            this.production = production;

            for (Map.Entry<LigneSimu,Integer> e : mapProduitChaine.entrySet()){
               LigneSimu p = e.getKey();
               int nombreProduit = e.getValue() * variation;
               p.setStock(Integer.parseInt(p.getStockTotal()),nombreProduit);

            }

            this.variation-=variation;
            this.setStock(Integer.parseInt(this.getStockTotal()),-variation);
            this.calculMarge();
        }
    }

    public String getCodeProduit() {
        if(espace){
            return "";
        }
        return codeProduit;
    }

    public String getQuantite() {
        if(espace || modeSomme){
            return "";
        }
        return quantite+"";
    }



    public String getStockTotal() {
        if(espace || modeSomme){
            return "";
        }
        return p.getQuantite()+"";
    }

    public void setStock(int qte, int nombre) {
        this.variation = nombre;
        this.stock = qte+nombre;
        //p.setQuantite(qte+nombre);
    }

    public String getStockUtilise() {
        if(espace){
            return "";
        }
        return stock+"";
    }

    public String getProduction() {
        if(espace || !modeProd) {
            return "";
        }
        return production+"";
    }

    public String getCoutUnit() {
        if(espace || modeSomme){
            return "";
        }
        return coutUnit+"";
    }

    public String getMarge() {
        if(espace){
            return "";
        }
        if(modeSomme){
            calculSomme();
        }
        return marge+"";
    }

    private void calculMarge(){
        float marge, prixTotalP = 0;

        for (Map.Entry<LigneSimu,Integer> e : mapProduitChaine.entrySet()){
            LigneSimu p = e.getKey();
            prixTotalP+= p.coutUnit*e.getValue();
        }

        marge = (p.getpVente() - prixTotalP)*this.production;

        this.marge=marge;
    }

    private void calculSomme(){
        marge = 0;
        for (LigneSimu ligneSimu:this.arraySomme){
            if(ligneSimu.modeProd){
                System.out.println(ligneSimu);
                marge += ligneSimu.marge;
            }
        }
    }
}

package com.triceratops.triceratops.modele;

import java.util.HashMap;
import java.util.Map;

public class LigneSimu {
    private String codeProduit;

    private Produit p;

    private HashMap<LigneSimu,Integer> mapProduitChaine;

    private int quantite, stock;

    private int variation, production;

    private float coutUnit, marge;

    private boolean espace,modeProd;

    /**
     * constructeur d'une ligne de simulation
     */
    public  LigneSimu(){
        espace = true;
    }

    /**
     * Constructeur d'une ligne de simulation
     * @param p produit de la ligne
     * @param quantite quantité de produit nécéssaire
     * @param marge marge realisé
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

    /**
     * Constructeur d'une ligne correspondant à une chaine de production
     * @param map
     * @param p produit de la ligne
     * @param quantite quantité de produit nécéssaire
     * @param marge marge realisé
     */
    public  LigneSimu(Produit p, HashMap<LigneSimu,Integer> map , int quantite, float marge){
        this(p, quantite, marge);
        this.mapProduitChaine = map;
        this.production = 0;
        this.modeProd = true;
    }

    /**
     *
     * @return Produit de la ligne de simulation
     */
    public Produit getP() {
        return p;
    }

    /**
     *
     * @return variation du stock
     */
    public String getVariation() {
        if(espace){
            return "";
        }
        return variation+"";
    }

    /**
     * Met à jour la variation
     * @param variation
     */
    public void setVariation(int variation) {
        this.variation = variation;
    }

    /**
     * Met à jour la production
     * @param production
     */
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
        }
    }

    /**
     *
     * @return code du produit de la ligne
     */
    public String getCodeProduit() {
        if(espace){
            return "";
        }
        return codeProduit;
    }

    /**
     *
     * @return quantité du produit de la ligne
     */
    public String getQuantite() {
        if(espace){
            return "";
        }
        return quantite+"";
    }


    /**
     *
     * @return stock total du produit
     */
    public String getStockTotal() {
        if(espace){
            return "";
        }
        return p.getQuantite()+"";
    }

    /**
     * Met a jour le stock
     * @param qte
     * @param nombre
     */
    public void setStock(int qte, int nombre) {
        this.variation = nombre;
        this.stock = qte+nombre;
        p.setQuantite(qte+nombre);
    }

    public String getStockUtilise() {
        if(espace){
            return "";
        }
        return stock+"";
    }

    /**
     *
     * @return nombre de produits produit
     */
    public String getProduction() {
        if(espace || !modeProd) {
            return "";
        }
        return production+"";
    }

    /**
     *
     * @return cout unitaire de la production
     */
    public String getCoutUnit() {
        if(espace){
            return "";
        }
        return coutUnit+"";
    }

    /**
     *
     * @return marge de la production
     */
    public String getMarge() {
        if(espace){
            return "";
        }
        return marge+"";
    }
}

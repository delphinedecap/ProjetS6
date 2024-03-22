package com.triceratops.triceratops.modele;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ChaineProduction {
    @JsonProperty("code")
    private String code;
    @JsonProperty("nom")
    private String nom;
    @JsonProperty("produitIn")
    private Map<String, Integer> produitIn = new HashMap<>();
    @JsonProperty("produitOut")
    private Map<String, Integer> produitOut = new HashMap<>();

    @JsonProperty("duree")
    private int duree;

    /**
     * Constructeur par defaut d'une chaine de production
     * Les attributs sont initilisés à null ou à une valeur équivalente
     */
    public ChaineProduction() {
    }

    /**
     * Constructeur d'une chaine de production
     * @param code code de la chaine de production
     * @param nom nom de la chaine de production
     * @param produitIn liste des produits en entréee de la chaine de production
     * @param produitOut liste des produits en sortie de la chaine de production
     */
    public ChaineProduction(String code, String nom, Map<String, Integer> produitIn, Map<String, Integer> produitOut, int duree) {
        this.code = code;
        this.nom = nom;
        this.produitIn = produitIn;
        this.produitOut = produitOut;
        this.duree = duree;
    }

    /**
     *
     * @return Liste des produits et des quantités nécessaires à la chaine de production
     */
    public Map<String, Integer> getProduitIn() {
        return this.produitIn;
    }

    /**
     *
     * @return liste des produits finis produis par la chaine de production et leurs quantité
     */
    public Map<String, Integer> getProduitOut() {
        return this.produitOut;
    }


    /**
     *
     * @return code de la chaine de production
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @return nom de la chaine de production
     */
    public String getNom() {
        return nom;
    }

    /**
     *
     * @return duree de la chaine de production en minutes
     */
    public int getDuree(){
        return this.duree;
    }

    /**
     * Permet de mettre à jour la liste des produits nécessaire à la chaine de production
     * @param produitIn liste des produits mises à jour
     */
    public void setProduitIn(Map<String, Integer> produitIn) {
        this.produitIn = produitIn;
    }

    /**
     * Permet de modifier la liste de produits en sortie de la chaine de production
     * @param produitOut liste de produits créés par la chaine de production
     */
    public void setProduitOut(Map<String, Integer> produitOut) {
        this.produitOut = produitOut;
    }

    /**
     * Permet de mettre à jour le code de la chaine de production
     * @param code nouveau code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Permet de mettre à jour le nom de la chaine de production
     * @param nom nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Permet de mettre à jour la duree de la chaine de production
     * @param duree nouvelle duree
     */
    public void setDuree(int duree){
        this.duree = duree;
    }

    /**
     * Methode toString
     * @return description de la chaine de production
     */
    @Override
    public String toString() {
        return "ChaineProduction{" +
                "code = '" + this.code + '\'' +
                ", nom = '" + this.nom + '\'' +
                ", produitIn = " + this.produitIn +
                ", produitOut = " + this.produitOut +
                ", duree = " + this.duree + "min" +
                '}';
    }

    /**
     * Permet de savoir si une chaine de production est réalisable à partir d'un stock donné
     * @param stock
     * @return vrai si elle est réalisable, faux sinon
     */
    public boolean estRealisable(HashMap<String, Integer> stock) {
        for (String produit : this.produitIn.keySet()){
            if(stock.containsKey(produit)){
                if (stock.get(produit)<this.produitIn.get(produit)){
                    return false;
                }
            }
        }
        return true;
    }
}

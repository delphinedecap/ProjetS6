package com.triceratops.triceratops.modele;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

    public ChaineProduction() {
    }

    public ChaineProduction(String code, String nom, Map<String, Integer> produitIn, Map<String, Integer> produitOut) {
        this.code = code;
        this.nom = nom;
        this.produitIn = produitIn;
        this.produitOut = produitOut;
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
     * @return liste des produits finis produis par la chaine de production
     */
    public Map<String, Integer> getProduitOut() {
        return this.produitOut;
    }


    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    /**
     * Permet de mettre à jour la liste des produits nécessaire à la chaine de production
     * @param produitIn liste des produits mises à jour
     */
    public void setProduitIn(Map<String, Integer> produitIn) {
        this.produitIn = produitIn;
    }

    /**
     * Permet de modifier le produit en sortie de la chaine de production
     * @param produitOut : liste de produits créés par la chaine de production
     */
    public void setProduitOut(Map<String, Integer> produitOut) {
        this.produitOut = produitOut;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "ChaineProduction{" +
                "code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", produitIn=" + produitIn +
                ", produitOut=" + produitOut +
                '}';
    }
}

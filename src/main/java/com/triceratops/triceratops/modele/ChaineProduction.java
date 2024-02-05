package com.triceratops.triceratops.modele;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

public class ChaineProduction {

    @JsonProperty("ProduitIn")
    private Map<String, Integer> produitIn = new HashMap<>();
    @JsonProperty("produitOut")
    private Produit produitOut;

    /**
     * constructeur de la chaine de production
     * @param produitOut produit créer par la chaine de production
     */
    public ChaineProduction(Produit produitOut) {
        this.produitOut = produitOut;
    }

    public ChaineProduction() {
    }

    /**
     * constructeur de la chaine de production
     * @param produitOut produit créer par la chaine de production
     * @param produitIn liste des produits nécessaire pour le fonctionnement de la chaine de production
     */
    @JsonCreator
    public ChaineProduction(@JsonProperty("ProduitIn") Map<String, Integer> produitIn,
                            @JsonProperty("produitOut") Produit produitOut) {
        this.produitIn = produitIn;
        this.produitOut = produitOut;
    }

    /**
     *
     * @return Liste des produits et des quantités nécessaires à la chaine de production
     */
    public Map<String, Integer> getProduitIn() {
        return produitIn;
    }

    /**
     *
     * @return produit final produit par la chaine de production
     */
    public Produit getProduitOut() {
        return produitOut;
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
     * @param produitOut : produit créé par la chaine de production
     */
    public void setProduitOut(Produit produitOut) {
        this.produitOut = produitOut;
    }
}

package com.triceratops.triceratops.modele;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ChaineProduction {
    @JsonProperty("ProduitIn")
    private Map<String, Integer> produitIn = new HashMap<>();
    @JsonProperty("produitOut")
    private Produit produitOut;

    public ChaineProduction(Produit produitOut) {
        this.produitOut = produitOut;
    }

    public ChaineProduction() {
    }

    @JsonCreator
    public ChaineProduction(@JsonProperty("ProduitIn") Map<String, Integer> produitIn,
                            @JsonProperty("produitOut") Produit produitOut) {
        this.produitIn = produitIn;
        this.produitOut = produitOut;
    }
    public Map<String, Integer> getProduitIn() {
        return produitIn;
    }
    public Produit getProduitOut() {
        return produitOut;
    }

    public void setProduitIn(Map<String, Integer> produitIn) {
        this.produitIn = produitIn;
    }

    public void setProduitOut(Produit produitOut) {
        this.produitOut = produitOut;
    }
}

package com.triceratops.triceratops.modele;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Produit {
    @JsonProperty("quantite")
    private int quantite;
    @JsonProperty("code")
    private String code;
    @JsonProperty("nom")
    private String nom;
    @JsonProperty("pAchat")
    private float pAchat;
    @JsonProperty("pVente")
    private float pVente;
    @JsonProperty("unite")
    private String unite; // Remplacer éventuellement par un enum

    public Produit() {
    }

    public Produit(int quantite, String code, String nom, float pAchat, float pVente, String unite) {
        this.quantite = quantite;
        this.code = code;
        this.nom = nom;
        this.pAchat = pAchat;
        this.pVente = pVente;
        this.unite = unite;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getpAchat() {
        return pAchat;
    }

    public void setpAchat(float pAchat) {
        this.pAchat = pAchat;
    }

    public float getpVente() {
        return pVente;
    }

    public void setpVente(float pVente) {
        this.pVente = pVente;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }
}

package com.triceratops.triceratops.modele;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Prix {
    @JsonProperty("code")
    private String code;
    @JsonProperty("pAchat")
    private double pAchat;
    @JsonProperty("pVente")
    private double pVente;
    @JsonProperty("quantiteCommande")
    private int quantiteCommande;

    /**
     * Constructeur par defaut
     * Les attributs sont initialosés à null ou à une valeur équivalente
     */
    public Prix() {
    }

    /**
     * Constructeur d'une instance de prix
     * @param code code du produit concerné
     * @param pAchat prix d'achat du produit
     * @param pVente prix de vente du produit
     * @param quantiteCommande quantité commandée du produit
     */
    public Prix(String code, double pAchat, double pVente, int quantiteCommande) {
        this.code = code;
        this.pAchat = pAchat;
        this.pVente = pVente;
        this.quantiteCommande = quantiteCommande;
    }

    /**
     *
     * @return code du produit
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @return prix d'achat du produit
     */
    public double getpAchat() {
        return pAchat;
    }

    /**
     *
     * @return prix de vente du produit
     */
    public double getpVente() {
        return pVente;
    }

    /**
     *
     * @return quantité commandée du produit
     */
    public int getQuantiteCommande() {
        return quantiteCommande;
    }

    /**
     * Met à jour la quantité commandée
     * @param quantiteCommande nouvelle quantité commandée
     */
    public void setQuantiteCommande(int quantiteCommande) {
        this.quantiteCommande = quantiteCommande;
    }

    /**
     * Met  à jour le code de l'article
     * @param code nouveau code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Met à jour le prix d'achat
     * @param pAchat nouveau prix d'achat
     */
    public void setpAchat(double pAchat) {
        this.pAchat = pAchat;
    }

    /**
     * Met à jour le prix de vente
     * @param pVente nouveau prix de vente
     */
    public void setpVente(double pVente) {
        this.pVente = pVente;
    }

    /**
     * Methode to string
     * @return description du prix
     */
    @Override
    public String toString() {
        return "Prix{" +
                "code='" + code + '\'' +
                ", pAchat=" + pAchat +
                ", pVente=" + pVente +
                ", quantiteCommande=" + quantiteCommande +
                '}';
    }
}

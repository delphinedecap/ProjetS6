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

    public Prix() {
    }

    public Prix(String code, double pAchat, double pVente, int quantiteCommande) {
        this.code = code;
        this.pAchat = pAchat;
        this.pVente = pVente;
        this.quantiteCommande = quantiteCommande;
    }

    public String getCode() {
        return code;
    }

    public double getpAchat() {
        return pAchat;
    }

    public double getpVente() {
        return pVente;
    }

    public int getQuantiteCommande() {
        return quantiteCommande;
    }

    public void setQuantiteCommande(int quantiteCommande) {
        this.quantiteCommande = quantiteCommande;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setpAchat(double pAchat) {
        this.pAchat = pAchat;
    }

    public void setpVente(double pVente) {
        this.pVente = pVente;
    }

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

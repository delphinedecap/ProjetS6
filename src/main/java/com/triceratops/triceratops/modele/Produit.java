package com.triceratops.triceratops.modele;


import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Produit {

    @JsonProperty("code")
    private String code;
    @JsonProperty("nom")
    private String nom;
    @JsonProperty("quantite")
    private int quantite;
    @JsonProperty("pAchat")
    private float pAchat;
    @JsonProperty("pVente")
    private float pVente;
    @JsonProperty("unite")
    private String unite; // Remplacer éventuellement par un enum

    /**
     * Constructeur par defaut du produit
     */
    public Produit() {
    }

    /**
     * Constructeur
     * @param quantite : quantité actuelle du produit
     * @param code : code générique du produit
     * @param nom : nom du produit
     * @param pAchat : prix d'achat du produit
     * @param pVente : prix de vente du produit
     * @param unite : type d'unité du produit (litre, kilogramme ...)
     */
    public Produit(int quantite, String code, String nom, float pAchat, float pVente, String unite) {
        this.quantite = quantite;
        this.code = code;
        this.nom = nom;
        this.pAchat = pAchat;
        this.pVente = pVente;
        this.unite = unite;
    }

    /**
     * Methode toString
     * @return la descrition du produit
     */
    @Override
    public String toString() {
        return "Produit{" +
                "quantite=" + quantite +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", pAchat=" + pAchat +
                ", pVente=" + pVente +
                ", unite='" + unite + '\'' +
                '}';
    }

    /**
     *
     * @return quantité actuelle du produit
     */
    public int getQuantite() {
        return quantite;
    }

    /**
     * Met à jour la quantité du produit
     * @param quantite : nouvelle quantité du produit
     */
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    /**
     *
     * @return code générique du produit
     */
    public String getCode() {
        return code;
    }

    /**
     * Met à jour le code du produit
     * @param code : nouveau code du produit
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return nom du produit
     */
    public String getNom() {
        return nom;
    }

    /**
     * Met à jour le nom du produit
     * @param nom : nouveau nom du produit
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     *
     * @return prix d'achat du produit
     */
    public float getpAchat() {
        return pAchat;
    }

    /**
     * Met à jour le prix d'achat du produit
     * @param pAchat : nouveau prix d'achat
     */
    public void setpAchat(float pAchat) {
        this.pAchat = pAchat;
    }

    /**
     *
     * @return prix de vente du produit
     */
    public float getpVente() {
        return pVente;
    }

    /**
     * Met à jour le prix de vente du produit
     * @param pVente : nouveau prix de vente du produit
     */
    public void setpVente(float pVente) {
        this.pVente = pVente;
    }

    /**
     *
     * @return unité du produit
     */
    public String getUnite() {
        return unite;
    }

    /**
     * Met à jour l'unité du produit
     * @param unite : nouvelle unité du produit
     */
    public void setUnite(String unite) {
        this.unite = unite;
    }
}

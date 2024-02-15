package com.triceratops.triceratops.modele;
import com.triceratops.triceratops.persistance.FileException;

import static com.triceratops.triceratops.persistance.InterfacePersistance.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSet {
    private static ArrayList<Prix> prix = null;
    private static ArrayList<ChaineProduction> chaines = null;
    private static HashMap<String,Produit> produits = null;

    /**
     *
     * @return liste des Prix
     */
    public static ArrayList<Prix> getPrix() {
        return prix;
    }

    /**
     *
     * @return liste des Chaines de Production
     */
    public static ArrayList<ChaineProduction> getChaines() {
        return chaines;
    }

    /**
     *
     * @return liste des produits avec leurs clés
     */
    public static HashMap<String, Produit> getProduits() {
        return produits;
    }

    /**
     * Permet d'extraire les données des différents fichiers
     * @param fichierProduit fichier contenant la liste des produits à extraire
     * @param fichierPrix fichier contenant la liste des prix à extraire
     * @param fichierChaine fichier contenant la liste des chaines de production à extraire
     * @throws FileException si le fichier n'existe pas, une exception est levée
     */
    public static void extractDataset(String fichierProduit, String fichierPrix, String fichierChaine) throws FileException {
        if (new File(fichierProduit).exists()){
            DataSet.produits = deserializeWithKeyFromFile(Produit.class,fichierProduit,"getCode");
        } else {
            throw new FileException(fichierProduit);
        }
        if (new File(fichierPrix).exists()){
            DataSet.prix = deserializeFromFile(Prix.class,fichierPrix);
        } else {
            throw new FileException(fichierPrix);
        }
        if (new File(fichierChaine).exists()){
            DataSet.chaines = deserializeFromFile(ChaineProduction.class,fichierChaine);
        } else {
            throw new FileException(fichierChaine);
        }
    }

    /**
     * Permet de stocker les données sous formes de fichier
     * @param fichierProduit nom du fichier à creer pour stocker les produits
     * @param fichierPrix nom du fichier à creer pour stocker les prix
     * @param fichierChaine nom du fichier à creer pour stocker les chaines de production
     */
    public static void archiveDataset (String fichierProduit, String fichierPrix, String fichierChaine) {
        //  /!\ A voir s'il faut ecraser les fichiers ou s'il faut une exception pour archiver sous un autre nom /!\
        serializeToFile(new ArrayList<>(produits.values()),fichierProduit);
        serializeToFile(prix,fichierPrix);
        serializeToFile(chaines,fichierChaine);
    }

    /**
     * Remise à null des attributs
     */
    public static void clearDataset() {
        produits = null;
        prix = null;
        chaines = null;
    }

}

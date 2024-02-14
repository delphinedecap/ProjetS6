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

    public static ArrayList<Prix> getPrix() {
        return prix;
    }

    public static ArrayList<ChaineProduction> getChaines() {
        return chaines;
    }

    public static HashMap<String, Produit> getProduits() {
        return produits;
    }

    public static void extractDataset(String fichierProduit, String fichierPrix, String fichierChaine) throws FileException {
        if (new File(fichierProduit).exists()){
            produits = deserializeWithKeyFromFile(Produit.class,fichierProduit,"getCode");
        } else {
            throw new FileException(fichierProduit);
        }
        if (new File(fichierPrix).exists()){
            prix = deserializeFromFile(Prix.class,fichierPrix);
        } else {
            throw new FileException(fichierPrix);
        }
        if (new File(fichierChaine).exists()){
            chaines = deserializeFromFile(ChaineProduction.class,fichierChaine);
        } else {
            throw new FileException(fichierChaine);
        }
    }

    public static void archiveDataset (String fichierProduit, String fichierPrix, String fichierChaine) {
        //  /!\ A voir s'il faut ecraser les fichiers ou s'il faut une exception pour archiver sous un autre nom /!\
        serializeToFile(new ArrayList<>(produits.values()),fichierProduit);
        serializeToFile(prix,fichierPrix);
        serializeToFile(chaines,fichierChaine);
    }

    public static void clearDataset() {
        produits = null;
        prix = null;
        chaines = null;
    }

}

package com.triceratops.triceratops.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triceratops.triceratops.modele.Produit;

import java.io.File;
import java.io.IOException;


public class ProduitSerializer {
    public static void serializeToFile(Produit produit) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Sérialisation de l'objet Produit en chaîne JSON
            String jsonString = objectMapper.writeValueAsString(produit);

            // Écriture de la chaîne JSON dans un fichier
            objectMapper.writeValue(new File("src/main/java/com/triceratops/triceratops/persistance/produit.json"), jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

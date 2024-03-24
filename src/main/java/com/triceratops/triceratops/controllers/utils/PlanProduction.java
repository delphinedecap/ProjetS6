package com.triceratops.triceratops.controllers.utils;

import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.Produit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.triceratops.triceratops.modele.DataSet.getProduits;

public class PlanProduction {

    /**
     * Simule heure par heure l'avancement des chaines de production
     * @return chaine de production et la liste de leurs temps finaux d'execution
     */
    public static HashMap<ChaineProduction, ArrayList<Integer>> simulation(HashMap<ChaineProduction, Integer> productions){
        HashMap<String, Produit> produits = getProduits();
        HashMap<String, Integer> stockSimule  = new HashMap<>();
        for (Produit p : produits.values()){
            stockSimule.put(p.getCode(), p.getQuantite());
        }
        HashMap<ChaineProduction, Integer> productionsEnCours = new HashMap<>();
        HashMap<ChaineProduction, ArrayList<Integer>> productionsFinies= new HashMap<>();
        HashMap<ChaineProduction, Integer> productionAFaire = (HashMap<ChaineProduction, Integer>) productions.clone();
        for (int i=0; i<60; i++){
            //gestion des chaines deja lancées
            HashMap<ChaineProduction, Integer> productionsEnCoursCopy = new HashMap<>(productionsEnCours);
            for (ChaineProduction c : productionsEnCoursCopy.keySet()) {
                if (productionsEnCours.get(c) == 1) {
                    productionsEnCours.remove(c);
                    if (productionsFinies.containsKey(c)) {
                        productionsFinies.get(c).add(i);
                    } else {
                        ArrayList<Integer> l = new ArrayList<>();
                        l.add(i);
                        productionsFinies.put(c, l);
                    }
                    //ajout des produits créés au stock
                    for (String p : c.getProduitOut().keySet()) {
                        if (stockSimule.containsKey(p)) {
                            int j = stockSimule.get(p) + c.getProduitOut().get(p);
                            stockSimule.replace(p, j);
                        } else {
                            stockSimule.put(p, c.getProduitOut().get(p));
                        }
                    }
                } else {
                    productionsEnCours.replace(c,productionsEnCours.get(c)-1);
                }
            }

            // lancement des nouvelles chaines possibles
            HashMap<ChaineProduction, Integer> productionAFaireCopy = new HashMap<>(productionAFaire);
            for (ChaineProduction c : productionAFaireCopy.keySet()) {
                if (productionAFaireCopy.get(c) == 0){
                    productionAFaire.remove(c);
                } else {
                    if (c.estRealisable(stockSimule) && !productionsEnCours.containsKey(c)) {
                        productionsEnCours.put(c, c.getDuree());
                        productionAFaire.replace(c, productionAFaire.get(c) - 1);
                        if (productionAFaire.get(c) == 0) {
                            productionAFaire.remove(c);
                        }
                        //mise à jour des stocks
                        for (String p : c.getProduitIn().keySet()) {
                            int j = stockSimule.get(p) - c.getProduitIn().get(p);
                            stockSimule.replace(p, j);
                        }
                    }
                }
            }
        }

        if(productionAFaire.isEmpty() && productionsEnCours.isEmpty()){
            return productionsFinies;
        }
        return null;
    }

    /**
     * Fonction qui permet de renvoyer le temps de production global estimé
     * @return -1 si ce temps dépasse 60h, le nombre d'heures sinon
     */
    public static int getTempsProduction(HashMap<ChaineProduction, Integer> productions){
        HashMap<ChaineProduction, ArrayList<Integer>> liste = simulation(productions);
        if (liste==null){
            return -1;
        }
        int max = 0;
        for (ArrayList<Integer> l : liste.values()){
            if (l.get(l.size()-1)>max){
                max = l.get(l.size()-1);
            }
        }
        return max;
    }

    /**
     * Permet de savoir si la production demandée est possible avec les stocks actuels
     * @return vraie si c'est possible, faux sinon
     */
    public static boolean estPossible(HashMap<ChaineProduction, Integer> productions){
        HashMap<String, Integer> produits = new HashMap<>();
        //Calcul de la variation de stock engendré par la production
        for (Map.Entry<ChaineProduction, Integer> mapEntry : productions.entrySet()) {
            ChaineProduction chaine = mapEntry.getKey();
            Integer production = mapEntry.getValue();
            Map<String, Integer> produitsIn = chaine.getProduitIn();
            //Calcul des produits utilisés par la chaine
            for ( String code: produitsIn.keySet()) {
                if (produits.containsKey(code)){
                    produits.put(code, produits.get(code)-produitsIn.get(code) * production );
                } else {
                    produits.put(code, produitsIn.get(code) * production * -1);
                }
            }

            Map<String, Integer> produitsOut = chaine.getProduitOut();
            //Calcul des produits produit par la chaine
            for ( String code: produitsOut.keySet()) {
                if (produits.containsKey(code)){
                    produits.put(code, produits.get(code)+ produitsOut.get(code) * production );
                } else {
                    produits.put(code,produitsOut.get(code) * production );
                }
            }
        }

        //Comparaison au stock actuel
        //Si un stock actuel + variation est négatif alors la production n'est pas possible
        HashMap<String, Produit> produitsReels = getProduits();
        for (Map.Entry<String, Integer> mapEntry : produits.entrySet()) {
            String code = mapEntry.getKey();
            Integer i = mapEntry.getValue();
            if (i+produitsReels.get(code).getQuantite()<0) {
                return false;
            }
        }
        return true;
    }
}

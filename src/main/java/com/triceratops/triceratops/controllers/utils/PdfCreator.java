package com.triceratops.triceratops.controllers.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.triceratops.triceratops.modele.ChaineProduction;
import com.triceratops.triceratops.modele.Prix;
import com.triceratops.triceratops.modele.Produit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.triceratops.triceratops.modele.DataSet.getPrix;
import static com.triceratops.triceratops.modele.DataSet.getProduits;
import static com.triceratops.triceratops.controllers.Simulateur.getMarge;

public class PdfCreator {

    /**
     * Edite le pdf correspondant à l'avancement heure par heure des chaines de productions
     * Le pdf sera sauvegardé dans C:\Users\Public\Downloads
     */
    public static void pdfSimulation(HashMap<ChaineProduction, ArrayList<Integer>> result, int tempsProduction){

        Document doc = new Document();
        try {
            //generate a PDF at the specified location
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("Simulation.pdf"));
            System.out.println("PDF created.");
            //opens the PDF
            doc.open();
            //adds paragraph to the PDF file

            Font bold16 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Font bold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font bold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            doc.add(new Paragraph("Simulation de fonctionnement : ", bold16));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Temps de fonctionnement global : " + tempsProduction + " heures", bold14));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Temps de fonctionnement par chaînes : ", bold14));
            doc.add(new Paragraph(" "));

            for (ChaineProduction c : result.keySet()) {
                doc.add(new Paragraph(c.getNom(), bold14));
                doc.add(new Paragraph("Durée de fonctionnement de la chaine : " + c.getDuree() * result.get(c).size() + "heures", bold12));
                int duree = c.getDuree();
                for (int i = 0; i < result.get(c).size(); i++) {
                    int tempsFin = result.get(c).get(i);
                    doc.add(new Paragraph("     Itération " + (i + 1) + " : de heure " + (tempsFin - duree) + " à heure " + tempsFin, bold12));
                }
                doc.add(new Paragraph(" "));
            }

            //close the PDF file
            doc.close();
            //closes the writer
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Edite le pdf correspondant à la production demandée
     * Le pdf sera sauvegardé dans C:\Users\Public\Downloads
     */
    public static void pdfResultat(HashMap<ChaineProduction, Integer> productions){

        ArrayList<Prix> prix = getPrix();
        HashMap<String, Produit> produits = getProduits();

        //created PDF document instance
        Document doc = new Document();
        try
        {
            //generate a PDF at the specified location
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("Results.pdf"));
            System.out.println("PDF created.");
            //opens the PDF
            doc.open();
            //adds paragraph to the PDF file

            Font bold16 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Font bold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font bold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            doc.add(new Paragraph("Résultat prévisionnel de production : ", bold16));
            doc.add(new Paragraph(" "));

            //
            for (Map.Entry<ChaineProduction, Integer> mapEntry : productions.entrySet()) {
                ChaineProduction chaine = mapEntry.getKey();
                Integer production = mapEntry.getValue();

                doc.add(new Paragraph(chaine.getNom() , bold14));
                doc.add(new Paragraph("Production demandée : " + production, bold14));
                doc.add(new Paragraph("Durée de la production unitaire : " + production*chaine.getDuree() +"heures", bold14));
                doc.add(new Paragraph("Produits entrants : " , bold12));

                PdfPTable tableEntrant = new PdfPTable(6);

                PdfPCell cell1 = new PdfPCell();
                Paragraph codeTexte = new Paragraph("Code ",bold12);
                cell1.addElement(codeTexte);
                cell1.setRowspan(3);
                cell1.setBorder(0);
                tableEntrant.addCell(cell1);

                PdfPCell cell2 = new PdfPCell();
                Paragraph nomTexte = new Paragraph("Nom ",bold12);
                cell2.addElement(nomTexte);
                cell2.setRowspan(3);
                cell2.setBorder(0);
                tableEntrant.addCell(cell2);

                PdfPCell cell3 = new PdfPCell();
                Paragraph quantiteTexte = new Paragraph("Quantité ",bold12);
                cell3.addElement(quantiteTexte);
                cell3.setRowspan(3);
                cell3.setBorder(0);
                tableEntrant.addCell(cell3);

                PdfPCell cell4 = new PdfPCell();
                Paragraph stockTexte = new Paragraph("Stock ",bold12);
                cell4.addElement(stockTexte);
                cell4.setRowspan(3);
                cell4.setBorder(0);
                tableEntrant.addCell(cell4);

                PdfPCell cell5 = new PdfPCell();
                Paragraph variationTexte = new Paragraph("Variation ",bold12);
                cell5.addElement(variationTexte);
                cell5.setRowspan(3);
                cell5.setBorder(0);
                tableEntrant.addCell(cell5);

                PdfPCell cell6 = new PdfPCell();
                Paragraph prixTexte = new Paragraph("Prix d'achat ",bold12);
                cell6.addElement(prixTexte);
                cell6.setRowspan(3);
                cell6.setBorder(0);
                tableEntrant.addCell(cell6);

                doc.add(tableEntrant);

                Map<String, Integer> produitsIn = chaine.getProduitIn();

                PdfPTable ligne;
                for ( String code: produitsIn.keySet()) {
                    Produit p = produits.get(code);
                    ligne= new PdfPTable(6);

                    cell1 = new PdfPCell();
                    codeTexte = new Paragraph(p.getCode() , font);
                    cell1.addElement(codeTexte);
                    cell1.setRowspan(3);
                    cell1.setBorder(0);
                    ligne.addCell(cell1);

                    cell2 = new PdfPCell();
                    nomTexte = new Paragraph(p.getNom(), font);
                    cell2.addElement(nomTexte);
                    cell2.setRowspan(3);
                    cell2.setBorder(0);
                    ligne.addCell(cell2);

                    cell3 = new PdfPCell();
                    quantiteTexte = new Paragraph(String.valueOf(produitsIn.get(code)),font);
                    cell3.addElement(quantiteTexte);
                    cell3.setRowspan(3);
                    cell3.setBorder(0);
                    ligne.addCell(cell3);

                    cell4 = new PdfPCell();
                    stockTexte = new Paragraph(String.valueOf(p.getQuantite()),font);
                    cell4.addElement(stockTexte);
                    cell4.setRowspan(3);
                    cell4.setBorder(0);
                    ligne.addCell(cell4);

                    cell5 = new PdfPCell();
                    double variation = produitsIn.get(code) * production * -1.0;
                    variationTexte = new Paragraph(String.valueOf(variation),font);
                    cell5.addElement(variationTexte);
                    cell5.setRowspan(3);
                    cell5.setBorder(0);
                    ligne.addCell(cell5);

                    Double prixAchat = 0.0;
                    for (Prix prix1 : prix){
                        if (Objects.equals(prix1.getCode(), code)){
                            prixAchat = prix1.getpAchat();
                        }
                    }
                    String prixAchatString;
                    if (prixAchat >0){
                        prixAchatString = String.valueOf(prixAchat);
                    } else {
                        prixAchatString = "NA";
                    }

                    cell6 = new PdfPCell();
                    prixTexte = new Paragraph(prixAchatString,font);
                    cell6.addElement(prixTexte);
                    cell6.setRowspan(3);
                    cell6.setBorder(0);
                    ligne.addCell(cell6);

                    doc.add(ligne);
                }


                //Tableau produits sortants
                doc.add(new Paragraph("Produits sortants : " , bold12));

                PdfPTable tableSortants = new PdfPTable(7);

                PdfPCell cells1 = new PdfPCell();
                Paragraph codeSortantTexte = new Paragraph("Code ",bold12);
                cells1.addElement(codeSortantTexte);
                cells1.setRowspan(3);
                cells1.setBorder(0);
                tableSortants.addCell(cells1);

                PdfPCell cells2 = new PdfPCell();
                Paragraph nomTexteSortant = new Paragraph("Nom ",bold12);
                cells2.addElement(nomTexteSortant);
                cells2.setRowspan(3);
                cells2.setBorder(0);
                tableSortants.addCell(cells2);


                PdfPCell cells3 = new PdfPCell();
                Paragraph stockTexteSortant = new Paragraph("Stock ",bold12);
                cells3.addElement(stockTexteSortant);
                cells3.setRowspan(3);
                cells3.setBorder(0);
                tableSortants.addCell(cells3);

                PdfPCell cells4 = new PdfPCell();
                Paragraph variationTextesortant = new Paragraph("Variation ",bold12);
                cells4.addElement(variationTextesortant);
                cells4.setRowspan(3);
                cells4.setBorder(0);
                tableSortants.addCell(cells4);

                PdfPCell cells5 = new PdfPCell();
                Paragraph prixTexteSortant = new Paragraph("Prix de vente ",bold12);
                cells5.addElement(prixTexteSortant);
                cells5.setRowspan(3);
                cells5.setBorder(0);
                tableSortants.addCell(cells5);

                PdfPCell cells6 = new PdfPCell();
                Paragraph margeTexte = new Paragraph("Marge  ",bold12);
                cells6.addElement(margeTexte);
                cells6.setRowspan(3);
                cells6.setBorder(0);
                tableSortants.addCell(cells6);

                PdfPCell cells7 = new PdfPCell();
                Paragraph commande = new Paragraph("Commande ",bold12);
                cells7.addElement(commande);
                cells7.setRowspan(3);
                cells7.setBorder(0);
                tableSortants.addCell(cells7);

                doc.add(tableSortants);

                Map<String, Integer> produitsOut = chaine.getProduitOut();

                for ( String code: produitsOut.keySet()) {
                    Produit p = produits.get(code);
                    ligne= new PdfPTable(7);

                    cell1 = new PdfPCell();
                    codeTexte = new Paragraph(p.getCode() , font);
                    cell1.addElement(codeTexte);
                    cell1.setRowspan(3);
                    cell1.setBorder(0);
                    ligne.addCell(cell1);

                    cell2 = new PdfPCell();
                    nomTexte = new Paragraph(p.getNom(), font);
                    cell2.addElement(nomTexte);
                    cell2.setRowspan(3);
                    cell2.setBorder(0);
                    ligne.addCell(cell2);

                    cell3 = new PdfPCell();
                    stockTexte = new Paragraph(String.valueOf(p.getQuantite()),font);
                    cell3.addElement(stockTexte);
                    cell3.setRowspan(3);
                    cell3.setBorder(0);
                    ligne.addCell(cell3);

                    cell4 = new PdfPCell();
                    double variation = produitsOut.get(code) * production ;
                    variationTexte = new Paragraph(String.valueOf(variation),font);
                    cell4.addElement(variationTexte);
                    cell4.setRowspan(3);
                    cell4.setBorder(0);
                    ligne.addCell(cell4);

                    Double prixVente = 0.0;
                    for (Prix prix1 : prix){
                        if (Objects.equals(prix1.getCode(), code)){
                            prixVente = prix1.getpVente();
                        }
                    }
                    String prixVenteString;
                    if (prixVente >0){
                        prixVenteString = String.valueOf(prixVente);
                    } else {
                        prixVenteString = "NA";
                    }

                    cell5 = new PdfPCell();
                    prixTexte = new Paragraph(prixVenteString,font);
                    cell5.addElement(prixTexte);
                    cell5.setRowspan(3);
                    cell5.setBorder(0);
                    ligne.addCell(cell5);

                    cell6 = new PdfPCell();
                    double marge = getMarge(prix,chaine,production);
                    margeTexte = new Paragraph(String.valueOf(marge), font);
                    cell6.addElement(margeTexte);
                    cell6.setRowspan(3);
                    cell6.setBorder(0);
                    ligne.addCell(cell6);

                    int quantiteCommande = 0;
                    for (Prix prix1 : prix){
                        if (Objects.equals(prix1.getCode(), code)){
                            quantiteCommande = prix1.getQuantiteCommande();
                        }
                    }
                    cells7 = new PdfPCell();
                    double commandes = (p.getQuantite() + variation) / quantiteCommande *100;
                    String StringCommande;
                    if (quantiteCommande ==0){
                        StringCommande = "NA";
                    } else {
                        StringCommande = String.valueOf(commandes)+"%";
                    }
                    commande = new Paragraph(StringCommande, font);
                    cells7.addElement(commande);
                    cells7.setRowspan(3);
                    cells7.setBorder(0);
                    ligne.addCell(cells7);

                    doc.add(ligne);
                }
                doc.add(new Paragraph(" "));
                doc.add(new Paragraph(" "));

            }

            //close the PDF file
            doc.close();
            //closes the writer
            writer.close();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

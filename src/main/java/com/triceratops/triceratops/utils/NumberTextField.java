package com.triceratops.triceratops.utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class NumberTextField extends TextField {

    public NumberTextField() {
        super();
        initialize();
    }

    private void initialize() {
        // Création d'un formatteur de texte pour valider et formater les entrées en nombres
        TextFormatter<Integer> numberFormatter = new TextFormatter<>(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == null) {
                    return "";
                }
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                if (string.isEmpty()) {
                    return null;
                }

                try {
                     return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return null; // Retourne null si la chaîne ne peut pas être convertie en nombre
                }
            }
        });

        // Définition du formatteur de texte pour le TextField
        this.setTextFormatter(numberFormatter);
    }
}

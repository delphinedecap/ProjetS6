package com.triceratops.triceratops.controllers;

import javafx.scene.control.Label;

public class About {

    public Label version;

    /**
     * Initialise le numéro de version au chargement de la pageq
     */
    public void initialize(){
        version.setText("Version : 1.0");
    }
}

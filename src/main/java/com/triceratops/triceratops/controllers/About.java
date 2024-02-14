package com.triceratops.triceratops.controllers;

import javafx.scene.control.Label;

public class About {


    public Label version;

    /**
     * Met à jour le numero de version
     */
    public void initialize(){
        version.setText("Version : 1.0");
    }
}

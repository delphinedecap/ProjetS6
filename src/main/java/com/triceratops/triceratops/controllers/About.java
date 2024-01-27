package com.triceratops.triceratops.controllers;

import javafx.fxml.FXML;

import javax.swing.text.html.ImageView;
import java.io.FileNotFoundException;

public class About {


    @FXML
    private ImageView logo;

    public void initialize() throws FileNotFoundException {
        //Creating an image
        /*Image image = new Image(new FileInputStream("img/logo.png"));

        //Setting the image view
        ImageView imageView = new ImageView(image);

        //Setting the position of the image
        imageView.setX(50);
        imageView.setY(25);

        //setting the fit height and width of the image view
        imageView.setFitHeight(455);
        imageView.setFitWidth(500);

        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        //Creating a Group object
        Group root = new Group(imageView);*/
    }
}

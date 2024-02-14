package com.triceratops.triceratops.utils;
import javafx.scene.layout.StackPane;

import java.util.HashMap;

public class NavigationUtils {

    private static final HashMap<String, ToggleNode> listView = new HashMap<>();

    private static StackPane contentPane;

    public NavigationUtils(StackPane contentPane){
        NavigationUtils.contentPane =contentPane;
    }

    public void add(String nom, ToggleNode toggleNode){
        NavigationUtils.listView.put(nom, toggleNode);
    }

    public static void goTo(String nom) {
        ToggleNode toggleNode = listView.get(nom);
        toggleNode.toggle().setSelected(true);
        contentPane.getChildren().setAll(toggleNode.node());
    }



    public static void goHome() {
        contentPane.getChildren().setAll(listView.get("Accueil").node());
    }
}

package com.triceratops.triceratops.persistance;

public class FileException extends Exception{
    public FileException(String message) {
        super("Fichier : " + message + " introuvable");
    }
}

package com.triceratops.triceratops.persistance;

public class FileException extends Exception{
    /**
     * Exception declenchée si un fichier n'existe pas ou est introuvable
     * @param message nom du fichier
     */
    public FileException(String message) {
        super("Fichier : " + message + " introuvable");
    }
}

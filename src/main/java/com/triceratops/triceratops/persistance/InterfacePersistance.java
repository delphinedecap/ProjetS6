package com.triceratops.triceratops.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class InterfacePersistance {
    //Ecrase par un nouveau JSON qui contiendra les entrées
    public static <T> void serializeToFile(ArrayList<T> objects, String output) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(output), objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Renvoie une Arraylist des données stockées en persistance

    public static <T> ArrayList<T> deserializeFromFile(Class<T> type,String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ArrayList<T> arrayList = objectMapper.readValue(new File(path),
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, type));
            return arrayList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Ajoute les nouvelles entrées et mets à jour les anciennes
    public static <T> void addToFile(ArrayList<T> objects, Class<T> objectType, String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        for (T object : deserializeFromFile(objectType,path)){
            if (objects.stream().noneMatch(p -> p.toString().equals(object.toString()))){
                objects.add(object);
            }
        }
        try {
            objectMapper.writeValue(new File(path), objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



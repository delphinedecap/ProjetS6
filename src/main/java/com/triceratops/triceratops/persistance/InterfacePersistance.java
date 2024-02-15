package com.triceratops.triceratops.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class InterfacePersistance {

    /**
     * Crée un fichier JSON avec une liste d'objets
     * Si le fichier existe déjà, il sera écrasé
     * @param objects : liste d'objets à serialiser dans le fichier
     * @param output : nom du fichier
     * @param <T> : type générique
     */
    public static <T> void serializeToFile(ArrayList<T> objects, String output) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(output), objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne une liste contenant les données stockées en persistance
     * @param type type des données
     * @param path fichier utilisé par la persistance
     * @return liste des données stockées
     */
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

    /**
     * Permet de deserialiser une liste d'objet à partir d'un fichier
     * @param type type de l'objet à déserialiser
     * @param path nom du fichier
     * @param getterKey nom de la méthode pout obtenir la clé
     * @return liste des objets avec une clé
     * @param <K> type de la clé
     * @param <T> type de l'objet
     */
    public static <K, T> HashMap<K,T> deserializeWithKeyFromFile(Class<T> type, String path,
                                                                   String getterKey) {
        ArrayList<T> resultArrayList = deserializeFromFile(type,path);
        HashMap<K,T> resultHash = new HashMap<K, T>();

        for (T t : resultArrayList) {
            try {
                K key = (K) type.getMethod(getterKey).invoke(t);
                resultHash.put(key, t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return resultHash;
    }

    /**
     * Permet de mettre à jour un fichier à partir d'une liste de nouvelles entrées
     * @param objects : liste des objets à ajouter ou mettre à jour
     * @param objectType : type des objets
     * @param path : fichier à mettre à jour
     */
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



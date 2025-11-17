package com.skillforge.db;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class jsonDatabaseManager<T extends DatabaseEntity> {
    private final String filePath ;
    private Class<T> entityClass ;

    // This is "our flag". We'll start with null to know if we've loaded the data or not.
    private ArrayList<T> dataList = null;
    private Gson gson ;

    jsonDatabaseManager(String filePath , Class<T> entityClass)
    {
        this.filePath = filePath ;
        this.entityClass = entityClass ;
        this.gson = new Gson() ;
        // We'll remove the loading from here to do "lazy loading".
        // this.dataList = new ArrayList<>() ;
    }

    jsonDatabaseManager(String filePath , Class<T> entityClass, Gson customGson)
    {
        this.filePath = filePath ;
        this.entityClass = entityClass ;
        this.gson = customGson;
    }

    // Lazy Loading
    //  Became private (so no one calls it from outside except our own methods)
    // It will only run once
    private void loadData()
    {
        // If data is not null, it means it's already loaded. Exit immediately.
        if (this.dataList != null) {
            return;
        }

        // If it's null, this is the first time we're requesting data.. start loading.
        try(FileReader reader = new FileReader(this.filePath)) {
            // to solve the type erasure problem , we will use the "Type" object so we can store
            //-> the Generics info that goes away at runtime
            Type listType = TypeToken.getParameterized(ArrayList.class , entityClass).getType() ;
            // a temporary one
            ArrayList<T> loadedData = gson.fromJson(reader , listType);
            // if there is a problem in accessing or there is nothing to load
            if(loadedData == null)
                loadedData = new ArrayList<>() ;

            this.dataList = loadedData ;

        } catch (FileNotFoundException e) {
            System.out.println("File " + filePath + " not found. We'll start with an empty list.");
            // We must start with an empty list so the program doesn't throw a NullPointer
            this.dataList = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error finding the File: " + e.getMessage());
            // If any other error happens, also start with an empty list.
            this.dataList = new ArrayList<>();
        }
    }

    // The Controller will call it only once (like at Logout)
    public void saveData()  {
        // If the data was never loaded (i.e., dataList is still null), do nothing.
        if (this.dataList == null) {
            System.out.println("Data was never loaded, nothing to save.");
            return;
        }

        // If the data exists, save it.
        try(FileWriter writer = new FileWriter(this.filePath))
        {
            // Gson will handle the writing style
            gson.toJson(dataList , writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Every public method must call loadData() first
    // which in turn will only run once

    public ArrayList<T> getDataList()
    {
        loadData(); // (Will only run the first time you request data)
        return dataList;
    }

    public T findById(String id) {
        loadData();
        for (T entity : dataList) {
            if (entity.getID().equals(id)) {
                return entity;
            }
        }
        return null;
    }

    public boolean add(T entity) {
        loadData();

        if (findById(entity.getID()) != null) {
            System.out.println("Validation Error: ID " + entity.getID() + " already exists.");
            return false;
        }

        dataList.add(entity);

        //writeData();
        return true;
    }

    public boolean update(T entity) {
        loadData();
        T existingEntity = findById(entity.getID());
        if (existingEntity == null) {
            return false;
        }

        int index = dataList.indexOf(existingEntity);
        dataList.set(index, entity);

        //writeData();
        return true;
    }

    public boolean delete(String id) {
        loadData();
        T entityToRemove = findById(id);
        if (entityToRemove == null) {
            return false;
        }

        dataList.remove(entityToRemove);

        //writeData();
        return true;
    }
}
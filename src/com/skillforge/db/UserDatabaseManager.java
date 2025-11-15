package com.skillforge.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skillforge.model.User;

public class UserDatabaseManager extends jsonDatabaseManager<User> {

    public UserDatabaseManager(String filePath) {
        super(filePath, User.class, createCustomGson());
    }


    private static Gson createCustomGson() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting(); // a look for the json formatting

        // here , we get our custom Gson
        //
        builder.registerTypeAdapter(User.class, new UserDeserializer());

        return builder.create();
    }
}
package com.skillforge.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skillforge.model.Instructor;
import com.skillforge.model.Student;
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

    public static void main(String[] args)
    {
        UserDatabaseManager db = new UserDatabaseManager("users.json");
        Student st = new Student("9666","Student","Omar","omarhesham2006@outlook.com","12345",null,null);
        db.add(st) ;
        db.saveData();
        System.out.println(db.getDataList());
    }

}
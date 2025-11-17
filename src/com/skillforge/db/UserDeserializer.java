package com.skillforge.db;

import com.google.gson.*;
import com.skillforge.model.Instructor;
import com.skillforge.model.Student;
import com.skillforge.model.User;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // turn your object into a json one
        JsonObject jsonObject = json.getAsJsonObject();

        // reading the "role"
        // if it is not there , so corrupted entry
        String role = jsonObject.get("role").getAsString();

        // as the type f role , Gson will transform the jsonObject into the specified one
        return switch (role.toLowerCase() /*unifying the logic */) { //enhanced switch 🤩
            case "student" ->
                    context.deserialize(jsonObject, Student.class);
            case "instructor" ->
                    context.deserialize(jsonObject, Instructor.class);
            default -> throw new JsonParseException("Unknown user role: " + role);
        };
    }
}
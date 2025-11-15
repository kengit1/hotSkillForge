package com.skillforge.model;

import com.skillforge.db.DatabaseEntity;

public abstract class User implements DatabaseEntity {
    private String userID ;
    private String userName ;
    private String role ;
    private String email ;
    private String passwordHash ;
    // getters
    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getID()
    {
        return getUserID() ;
    }
}

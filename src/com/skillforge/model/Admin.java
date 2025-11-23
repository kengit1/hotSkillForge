package com.skillforge.model;

public class Admin extends User {

    public Admin(String userId, String username, String email, String passwordHash) {
        setUserID(userId);
        setUserName(username);
        setRole("ADMIN");
        setEmail(email);
        setPasswordHash(passwordHash);
    }
    @Override
    public String getID() {
        return getUserID();
    }
    @Override
    public String toString() {
        return getID() + " " + getUserName() + " " + getRole() + " " + getEmail();
    }
}//

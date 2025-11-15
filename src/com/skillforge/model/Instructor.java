package com.skillforge.model;

public class Instructor extends User{
    private String userID;
    private String userName;
    private String role;
    private String email;
    private String passwordHash;
    public Instructor(String userID, String userName, String email, String passwordHash) {
        setUserID(userID);
        setUserName(userName);
        setRole("Instructor");
        setEmail(email);
        setPasswordHash(passwordHash);
    }

    @Override
    public String getID() {
        return getUserID();
    }
}

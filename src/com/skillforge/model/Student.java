package com.skillforge.model;

public class Student extends User{
    private String userID;
    private String userName;
    private String role;
    private String email;
    private String passwordHash;
    public Student(String userID, String userName, String email, String passwordHash) {
        setUserID(userID);
        setUserName(userName);
        setRole("Student");
        setEmail(email);
        setPasswordHash(passwordHash);
    }

    @Override
    public String getID() {
        return getUserID();
    }
}

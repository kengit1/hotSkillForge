package com.skillforge.model;
public abstract class User {
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

    protected void setUserID(String userID) { this.userID = userID; }

    protected void setUserName(String userName) { this.userName = userName; }

    protected void setRole(String role) { this.role = role; }

    protected void setEmail(String email) { this.email = email; }

    protected void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

}

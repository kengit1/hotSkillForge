package com.skillforge.model;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    // DO NOT re-declare userID, username, etc. They are inherited from User.

    private List<String> createdCourses;
    public Instructor(String userID, String userName, String email, String passwordHash) {
        setUserID(userID);
        setUserName(userName);
        setRole("Instructor");
        setEmail(email);
        setPasswordHash(passwordHash);
        this.createdCourses = new ArrayList<>();
    }

    /**
     * Constructor used by Gson when loading from the database.
     * FIXED: Uses protected setters.
     */
    public Instructor(String userId, String role, String username, String email, String passwordHash,
                      List<String> createdCourses) {
        setUserID(userId);
        setUserName(username);
        setRole(role);
        setEmail(email);
        setPasswordHash(passwordHash);
        this.createdCourses = (createdCourses != null) ? createdCourses : new ArrayList<>();
    }

    // --- Getters and helper methods ---

    public List<String> getCreatedCourses() {
        return createdCourses;
    }

    public void addCreatedCourse(String courseId) {
        if (!createdCourses.contains(courseId)) {
            createdCourses.add(courseId);
        }
    }

    /**
     * FIXED: Added the required getID() method from the DatabaseEntity interface.
     */
    @Override
    public String getID() {
        return getUserID();
    }
}
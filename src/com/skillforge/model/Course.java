package com.skillforge.model;

import com.skillforge.db.DatabaseEntity;

public class Course implements DatabaseEntity {
    private String courseID ;
    private String title ;
    private String description ;

    @Override
    public String getID() {
        return getCourseID();
    }

    // getters

    public String getCourseID() {
        return courseID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

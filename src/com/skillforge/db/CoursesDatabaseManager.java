package com.skillforge.db;

import com.skillforge.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CoursesDatabaseManager extends jsonDatabaseManager<Course> {

    public static final String COURSES_FILE_PATH = "courses.json";

    public CoursesDatabaseManager() {
        super(COURSES_FILE_PATH, Course.class);
    }

    public void updateCourseStatus(String courseId, String newStatus) {
        for (Course c : getDataList()) {
            if (c.getID().equals(courseId)) {
                c.setApprovalStatus(newStatus);
                saveData();
                return;
            }
        }
    }

    public List<Course> getPendingCourses() {
        List<Course> pending = new ArrayList<>();
        for (Course c : getDataList()) {
            if (c.getApprovalStatus().equalsIgnoreCase("Pending")) {
                pending.add(c);
            }
        }
        return pending;
    }
}

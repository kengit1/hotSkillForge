package com.skillforge.main;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.model.Course;

import java.util.List;

public class CourseApprovalService {
    private final CoursesDatabaseManager courseDB;

    public CourseApprovalService(CoursesDatabaseManager db) {
        this.courseDB = db;
    }//

    public List<Course> getPendingCourses() {
        return courseDB.getPendingCourses();
    }

    public void approveCourse(String courseId) {
        courseDB.updateCourseStatus(courseId, "APPROVED");
    }

    public void rejectCourse(String courseId) {
        courseDB.updateCourseStatus(courseId, "REJECTED");
    }
}

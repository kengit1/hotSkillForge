package com.skillforge.model;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User{
    private List<String> enrolledCourses; // List of courseIds
    private Map<String, List<String>> progress;
    public Student(String userID, String userName, String email, String passwordHash) {
        setUserID(userID);
        setUserName(userName);
        setRole("Student");
        setEmail(email);
        setPasswordHash(passwordHash);
        this.progress = new HashMap<>();


    }
    public void enrollCourse(Course course) {
        this.enrolledCourses.add(course.getID());
        UserDatabaseManager db = null;
        db.update(this);
        CoursesDatabaseManager cd=null;
        cd.update(course);
        db.saveData();
        cd.saveData();
    }
    public boolean toggleLessonCompleted(String courseId, String lessonId) {
        List<String> completedLessons = progress.get(courseId);
        if (completedLessons != null) {
            if (completedLessons.contains(lessonId)) {
                completedLessons.remove(lessonId);
            } else {
                completedLessons.add(lessonId);
            }
            return true;
        }
        return false;
    }
    public List<String> getEnrolledCourses() { return enrolledCourses; }
    public Map<String, List<String>> getProgress() { return progress; }






    @Override
    public String getID() {
        return getUserID();
    }
}

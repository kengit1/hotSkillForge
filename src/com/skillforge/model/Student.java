package com.skillforge.model;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class Student extends User {
    private List<String> enrolledCourses;
    private Map<String, List<String>> progress;
    private Map<String, Double> quizScores;
    public Student(String userId, String role, String username, String email, String passwordHash,List<String> enrolledCourses,Map<String,List<String>> progress) {
        setUserID(userId);
        setUserName(username);
        setRole(role);
        setEmail(email);
        setPasswordHash(passwordHash);
        this.enrolledCourses = (enrolledCourses != null) ? enrolledCourses : new ArrayList<>();
        this.progress=(progress !=null)? progress:new HashMap<>();
        this.enrolledCourses = (enrolledCourses != null) ? enrolledCourses : new ArrayList<>();
        this.progress = (progress != null) ? progress : new HashMap<>();
        // Initialize quizScores
        this.quizScores = (quizScores != null) ? quizScores : new HashMap<>();
    }
    public List<String> getEnrolledCourses() { return enrolledCourses;}
    public Map<String, List<String>> getProgress() {return progress; }

    @Override
    public String getID() {
        return getUserID();
    }
    public Map<String, Double> getQuizScores() { return quizScores; }

    public void addQuizScore(String lessonId, double score) {
        this.quizScores.put(lessonId, score);
    }

    @Override
    public String toString() {
        return this.getID()+" "+this.getUserName()+" "+
                this.getRole()+" "+this.getEmail()+" ";
    }
}

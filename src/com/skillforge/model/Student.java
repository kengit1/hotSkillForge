package com.skillforge.model;

import com.skillforge.model.User;
import java.util.*;

public class Student extends User {

    private List<String> enrolledCourses;               // course IDs
    private Map<String, List<String>> progress;         // courseId → list of completed lesson IDs
    private Map<String, Double> quizScores;             // lessonId → score

    // 🔥 FIXED & updated constructor
    public Student(String userId, String role, String username, String email, String passwordHash,
                   List<String> enrolledCourses, Map<String, List<String>> progress,
                   Map<String, Double> quizScores) {

        setUserID(userId);
        setUserName(username);
        setRole(role);
        setEmail(email);
        setPasswordHash(passwordHash);

        this.enrolledCourses = (enrolledCourses != null) ? enrolledCourses : new ArrayList<>();
        this.progress = (progress != null) ? progress : new HashMap<>();
        this.quizScores = (quizScores != null) ? quizScores : new HashMap<>();
    }

    // Overload constructor (old one)
    public Student(String userId, String role, String username, String email, String passwordHash,
                   List<String> enrolledCourses, Map<String, List<String>> progress) {
        this(userId, role, username, email, passwordHash,
                enrolledCourses,
                progress,
                new HashMap<>());
    }

    public List<String> getEnrolledCourses() { return enrolledCourses; }
    public Map<String, List<String>> getProgress() { return progress; }
    public Map<String, Double> getQuizScores() { return quizScores; }

    @Override
    public String getID() {
        return getUserID();
    }

    // 🔥 NEW: analytics helpers
    public void addCompletedLesson(String courseId, String lessonId) {
        progress.computeIfAbsent(courseId, k -> new ArrayList<>());
        if (!progress.get(courseId).contains(lessonId)) {
            progress.get(courseId).add(lessonId);
        }
    }

    public void addQuizScore(String lessonId, double score) {
        quizScores.put(lessonId, score);
    }

    @Override
    public String toString() {
        return this.getID()+" "+this.getUserName()+" "+
                this.getRole()+" "+this.getEmail()+" ";
    }
}

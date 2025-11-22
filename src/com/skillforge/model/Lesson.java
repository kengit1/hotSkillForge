package com.skillforge.model;

import com.skillforge.db.DatabaseEntity;
import java.util.ArrayList;
import java.util.List;

public class Lesson implements DatabaseEntity {

    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;

    // 🔥 NEW: analytics fields
    private List<String> completedStudents;      // student IDs
    private List<QuizResult> quizResults;        // student score history

    private Quiz quiz;

    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
        this.quiz = null;

        this.completedStudents = new ArrayList<>();
        this.quizResults = new ArrayList<>();
    }

    @Override
    public String getID() {
        return this.lessonId;
    }

    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }
    public Quiz getQuiz() { return quiz; }

    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }

    public void addResource(String resourcePath) {
        if (resourcePath != null && !resourcePath.isEmpty()) {
            this.resources.add(resourcePath);
        }
    }

    // 🔥 NEW: analytics getters
    public List<String> getCompletedStudents() { return completedStudents; }
    public List<QuizResult> getQuizResults() { return quizResults; }

    public void addCompletedStudent(String studentId) {
        if (!completedStudents.contains(studentId)) {
            completedStudents.add(studentId);
        }
    }

    public void addQuizResult(String studentId, double score) {
        quizResults.add(new QuizResult(studentId, score));
    }

    public boolean hasQuiz() {
        return this.quiz != null && !this.quiz.getQuestions().isEmpty();
    }

    public boolean removeResource(String resourcePath) {
        return this.resources.remove(resourcePath);
    }


    public static class QuizResult {
        private String studentId;
        private double score;

        public QuizResult(String studentId, double score) {
            this.studentId = studentId;
            this.score = score;
        }

        public String getStudentId() { return studentId; }
        public double getScore() { return score; }
    }
}

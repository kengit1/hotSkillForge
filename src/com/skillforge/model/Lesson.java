package com.skillforge.model;

import com.skillforge.db.DatabaseEntity; // Used to provide a unique ID structure
import java.util.ArrayList;
import java.util.List;

public class Lesson implements DatabaseEntity {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private Quiz quiz;


    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
        this.quiz = null;
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



    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void addResource(String resourcePath) {
        if (resourcePath != null && !resourcePath.isEmpty()) {
            this.resources.add(resourcePath);
        }
    }

    public boolean hasQuiz() {
        return this.quiz != null && !this.quiz.getQuestions().isEmpty();
    }
    public boolean removeResource(String resourcePath) {
        return this.resources.remove(resourcePath);
    }
}


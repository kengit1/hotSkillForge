package com.skillforge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quiz implements Serializable {
    private List<Question> questions;
    private int passingScore; // نسبة مئوية مثلاً 50%

    public Quiz() {
        this.questions = new ArrayList<>();
        this.passingScore = 50; // Default
    }

    public void addQuestion(Question q) {
        this.questions.add(q);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getPassingScore() { return passingScore; }
    public void setPassingScore(int score) { this.passingScore = score; }

    // Logic to calculate score based on answers
    public double calculateScore(List<Integer> studentAnswers) {
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (i < studentAnswers.size() && questions.get(i).isCorrect(studentAnswers.get(i))) {
                correctCount++;
            }
        }
        return ((double) correctCount / questions.size()) * 100;
    }
}
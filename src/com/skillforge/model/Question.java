package com.skillforge.model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private int correctOptionIndex; // 0 for A, 1 for B, etc.

    public Question(String questionText, List<String> options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    // Getters
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }

    // Logic check answer
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctOptionIndex;
    }
}
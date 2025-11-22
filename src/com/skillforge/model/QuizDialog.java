package com.skillforge.model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDialog extends JDialog {
    private Quiz quiz;
    private List<ButtonGroup> buttonGroups; // عشان نعرف الطالب اختار ايه
    private boolean passed = false;
    private double score = 0;

    public QuizDialog(JFrame parent, String lessonTitle, Quiz quiz) {
        super(parent, "Quiz: " + lessonTitle, true); // Modal dialog
        this.quiz = quiz;
        this.buttonGroups = new ArrayList<>();

        setupUI();
        setSize(500, 600);
        setLocationRelativeTo(parent);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        int qIndex = 1;
        for (Question q : quiz.getQuestions()) {
            JPanel qPanel = new JPanel(new GridLayout(0, 1));
            qPanel.setBorder(BorderFactory.createTitledBorder("Q" + qIndex + ": " + q.getQuestionText()));

            ButtonGroup group = new ButtonGroup();
            // بنخزن ترتيب الاختيارات عشان نعرف نقارن
            // خدعة بسيطة: هنستخدم ActionCommand عشان نخزن الـ Index
            int optIndex = 0;
            for (String opt : q.getOptions()) {
                JRadioButton rb = new JRadioButton(opt);
                rb.setActionCommand(String.valueOf(optIndex));
                group.add(rb);
                qPanel.add(rb);
                optIndex++;
            }
            buttonGroups.add(group);
            questionsPanel.add(qPanel);
            questionsPanel.add(Box.createVerticalStrut(10));
            qIndex++;
        }

        add(new JScrollPane(questionsPanel), BorderLayout.CENTER);

        JButton submitBtn = new JButton("Submit Quiz");
        submitBtn.addActionListener(e -> evaluateQuiz());
        add(submitBtn, BorderLayout.SOUTH);
    }

    private void evaluateQuiz() {
        List<Integer> answers = new ArrayList<>();
        for (ButtonGroup bg : buttonGroups) {
            if (bg.getSelection() == null) {
                JOptionPane.showMessageDialog(this, "Please answer all questions!");
                return;
            }
            answers.add(Integer.parseInt(bg.getSelection().getActionCommand()));
        }

        this.score = quiz.calculateScore(answers);
        this.passed = score >= quiz.getPassingScore();

        String message = "Score: " + score + "%\n" + (passed ? "Passed!" : "Failed. Try again.");
        JOptionPane.showMessageDialog(this, message);
        dispose(); // اقفل الشاشة
    }

    public boolean isPassed() { return passed; }
    public double getScore() { return score; }
}
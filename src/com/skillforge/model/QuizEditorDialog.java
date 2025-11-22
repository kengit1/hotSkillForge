package com.skillforge.model;

import com.skillforge.model.Question;
import com.skillforge.model.Quiz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizEditorDialog extends JDialog {
    private Quiz quiz;
    private DefaultListModel<String> questionsListModel;
    private JList<String> questionsList;

    // Fields for new question
    private JTextField questionField;
    private JTextField opt1Field, opt2Field, opt3Field, opt4Field;
    private JComboBox<String> correctAnsBox;
    private JTextField passingScoreField;

    public QuizEditorDialog(Frame owner, Quiz quiz) {
        super(owner, "Edit Quiz", true);
        this.quiz = quiz;

        setSize(600, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initUI();
        loadExistingQuestions();
    }

    private void initUI() {
        // --- Top Panel: Passing Score ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Passing Score (%): "));
        passingScoreField = new JTextField(String.valueOf(quiz.getPassingScore()), 5);
        topPanel.add(passingScoreField);
        JButton updateScoreBtn = new JButton("Update Score");
        updateScoreBtn.addActionListener(e -> {
            try {
                int score = Integer.parseInt(passingScoreField.getText());
                quiz.setPassingScore(score);
                JOptionPane.showMessageDialog(this, "Passing score updated!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number");
            }
        });
        topPanel.add(updateScoreBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: List of Questions ---
        questionsListModel = new DefaultListModel<>();
        questionsList = new JList<>(questionsListModel);
        JScrollPane scrollPane = new JScrollPane(questionsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Questions"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Add New Question Form ---
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Question"));

        formPanel.add(new JLabel("Question Text:"));
        questionField = new JTextField();
        formPanel.add(questionField);

        formPanel.add(new JLabel("Option 1:"));
        opt1Field = new JTextField();
        formPanel.add(opt1Field);

        formPanel.add(new JLabel("Option 2:"));
        opt2Field = new JTextField();
        formPanel.add(opt2Field);

        formPanel.add(new JLabel("Option 3:"));
        opt3Field = new JTextField();
        formPanel.add(opt3Field);

        formPanel.add(new JLabel("Option 4:"));
        opt4Field = new JTextField();
        formPanel.add(opt4Field);

        formPanel.add(new JLabel("Correct Option:"));
        String[] options = {"Option 1", "Option 2", "Option 3", "Option 4"};
        correctAnsBox = new JComboBox<>(options);
        formPanel.add(correctAnsBox);

        JButton addBtn = new JButton("Add Question");
        addBtn.addActionListener(e -> addQuestion());

        JButton closeBtn = new JButton("Close & Save");
        closeBtn.addActionListener(e -> dispose());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addBtn);
        buttonsPanel.add(closeBtn);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(formPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonsPanel, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    private void loadExistingQuestions() {
        questionsListModel.clear();
        int i = 1;
        for (Question q : quiz.getQuestions()) {
            questionsListModel.addElement("Q" + (i++) + ": " + q.getQuestionText());
        }
    }

    private void addQuestion() {
        String text = questionField.getText().trim();
        String o1 = opt1Field.getText().trim();
        String o2 = opt2Field.getText().trim();
        String o3 = opt3Field.getText().trim();
        String o4 = opt4Field.getText().trim();

        if (text.isEmpty() || o1.isEmpty() || o2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill at least Question and 2 Options.");
            return;
        }

        List<String> ops = new ArrayList<>();
        ops.add(o1);
        ops.add(o2);
        if (!o3.isEmpty()) ops.add(o3);
        if (!o4.isEmpty()) ops.add(o4);

        int correctIdx = correctAnsBox.getSelectedIndex();
        // Validate if correct index exists (in case user filled 2 options but selected Option 4 as correct)
        if (correctIdx >= ops.size()) {
            JOptionPane.showMessageDialog(this, "Correct option must be one of the filled options!");
            return;
        }

        Question q = new Question(text, ops, correctIdx);
        quiz.addQuestion(q);

        questionField.setText("");
        opt1Field.setText("");
        opt2Field.setText("");
        opt3Field.setText("");
        opt4Field.setText("");

        loadExistingQuestions();
    }
}
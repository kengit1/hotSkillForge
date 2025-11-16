package com.skillforge.gui;

import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Lesson;
import com.skillforge.model.Student;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for viewing lesson content and toggling completion status.
 */
public class LessonFrame extends JDialog {
    private Student student;
    private Course course;
    private UserDatabaseManager userDBManager;

    private JList<Lesson> lessonList;
    private DefaultListModel<Lesson> lessonListModel;
    private JTextArea contentArea;
    private JButton completeButton;

    public LessonFrame(JFrame parent, Student student, Course course, UserDatabaseManager userDBManager) {
        super(parent, "Lessons for: " + course.getTitle(), true);
        this.student = student;
        this.course = course;
        this.userDBManager = userDBManager;

        // Set up the main dialog properties
        setSize(850, 600);
        setLayout(new BorderLayout(15, 15));

        // --- Lesson List Panel (WEST) ---

        // 1. Populate the list model with lessons from the course
        lessonListModel = new DefaultListModel<>();
        for (Lesson lesson : course.getLessons()) {
            lessonListModel.addElement(lesson);
        }

        lessonList = new JList<>(lessonListModel);
        lessonList.setFixedCellHeight(30);
        lessonList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 2. Set custom cell renderer to show completion status
        List<String> completedLessons = student.getProgress().get(course.getID());
        lessonList.setCellRenderer(new LessonCellRenderer(completedLessons != null ? completedLessons : new ArrayList<>()));

        // 3. Add selection listener to display content when a lesson is clicked
        lessonList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Lesson selectedLesson = lessonList.getSelectedValue();
                if (selectedLesson != null) {
                    displayLessonContent(selectedLesson);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(lessonList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Course Modules (" + course.getLessons().size() + ")"));
        listScrollPane.setPreferredSize(new Dimension(250, 0));
        add(listScrollPane, BorderLayout.WEST);

        // --- Lesson Content Panel (CENTER) ---

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentArea = new JTextArea("Select a lesson from the left to view its content.");
        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        contentArea.setEditable(false);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // Completion Button
        completeButton = new JButton("Mark as Completed");
        completeButton.setEnabled(false);
        completeButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        completeButton.setForeground(Color.WHITE);
        completeButton.setFocusPainted(false);
        completeButton.addActionListener(e -> toggleLessonCompletion());

        contentPanel.add(completeButton, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        setLocationRelativeTo(parent);

        // Auto-select the first lesson if available
        if (!course.getLessons().isEmpty()) {
            lessonList.setSelectedIndex(0);
        }
    }

    /**
     * Updates the content area and the completion button based on the selected lesson.
     */
    private void displayLessonContent(Lesson lesson) {
        contentArea.setText("LESSON: " + lesson.getTitle() + "\n\n" + lesson.getContent() +
                "\n\n--- Resources ---\n" + (lesson.getResources().isEmpty() ? "None" : String.join("\n", lesson.getResources())));
        contentArea.setCaretPosition(0); // Scroll to top

        // Determine if the current lesson is completed
        boolean isCompleted = student.getProgress().getOrDefault(course.getID(), List.of()).contains(lesson.getID());

        // Update button text and color
        completeButton.setText(isCompleted ? "✅ Mark as Incomplete" : "Mark as Completed");
        completeButton.setBackground(isCompleted ? new Color(255, 140, 0) : new Color(60, 179, 113)); // Orange vs Green
        completeButton.setEnabled(true);
    }

    /**
     * Handles marking a lesson as complete or incomplete.
     */
    private void toggleLessonCompletion() {
        Lesson lesson = lessonList.getSelectedValue();
        if (lesson == null) return;

        // 1. Update Student model's progress map
        boolean updated = student.toggleLessonCompleted(course.getID(), lesson.getID());

        if (updated) {
            // 2. Persist the change to the User database
            userDBManager.update(student);

            // 3. UI Refresh
            lessonList.repaint(); // Rerender the list item (to show the checkmark change)
            displayLessonContent(lesson); // Update the button text

            JOptionPane.showMessageDialog(this, "Progress updated! Data saved at logout.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "Error: Course progress tracking not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Custom renderer to visually indicate completed lessons with a checkmark.
     */
    private static class LessonCellRenderer extends DefaultListCellRenderer {
        private List<String> completedLessonIds;

        public LessonCellRenderer(List<String> completedLessonIds) {
            this.completedLessonIds = completedLessonIds;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            Lesson lesson = (Lesson) value;
            String lessonId = lesson.getID();

            if (completedLessonIds.contains(lessonId)) {
                label.setText("  ✅ " + lesson.getTitle());
                label.setFont(label.getFont().deriveFont(Font.ITALIC));
                if (!isSelected) {
                    label.setBackground(new Color(224, 255, 224)); // Light green background
                }
            } else {
                label.setText("  ⬜ " + lesson.getTitle());
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
                if (!isSelected) {
                    label.setBackground(list.getBackground());
                }
            }
            // Add slight padding
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            return label;
        }
    }
}
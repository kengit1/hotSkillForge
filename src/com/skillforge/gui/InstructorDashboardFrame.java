package com.skillforge.gui;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Instructor;
import com.skillforge.service.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class InstructorDashboardFrame extends JFrame {
    private final Instructor currentInstructor;
    private final CourseService courseService;
    private final UserDatabaseManager userDB;
    private final CoursesDatabaseManager courseDB;

    private JList<String> courseList;
    private DefaultListModel<String> courseListModel;
    private List<Course> instructorCourses;

    public InstructorDashboardFrame(Instructor instructor, UserDatabaseManager userDB, CoursesDatabaseManager courseDB) {
        this.currentInstructor = instructor;
        this.userDB = userDB;
        this.courseDB = courseDB;
        this.courseService = new CourseService(courseDB, userDB);

        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setTitle("Instructor Dashboard - " + currentInstructor.getUserName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, Instructor " + currentInstructor.getUserName(), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        northPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout & Save Data");
        logoutButton.addActionListener(e -> {
            userDB.saveData();
            courseDB.saveData();
            JOptionPane.showMessageDialog(this, "Logged out successfully. All data saved.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            new first_panel().setVisible(true);
            dispose();
        });
        northPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Created Courses (Select one for actions)"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton createCourseButton = new JButton("Create New Course");
        JButton editCourseButton = new JButton("Edit Details");
        JButton manageLessonsButton = new JButton("Manage Lessons");
        JButton viewStudentsButton = new JButton("View Enrolled Students");

        actionPanel.add(createCourseButton);
        actionPanel.add(editCourseButton);
        actionPanel.add(manageLessonsButton);
        actionPanel.add(viewStudentsButton);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);

        createCourseButton.addActionListener(e -> handleCreateCourse());
        editCourseButton.addActionListener(e -> handleEditCourseDetails());
        viewStudentsButton.addActionListener(e -> handleViewStudents());
        manageLessonsButton.addActionListener(e -> handleManageLessons());
    }

    private void loadCourses() {
        courseListModel.clear();
        instructorCourses = courseService.getCoursesByInstructor(currentInstructor.getID());

        if (instructorCourses.isEmpty()) {
            courseListModel.addElement("No courses created yet. Click 'Create New Course' to begin.");
        } else {
            for (Course course : instructorCourses) {
                String status = course.getLessons().isEmpty() ? "DRAFT" : "ACTIVE";
                courseListModel.addElement(String.format("ID: %s | %s [%s] | Lessons: %d | Students: %d",
                        course.getID(), course.getTitle(), status, course.getLessons().size(), course.getStudents().size()));
            }
        }
    }

    private Course getSelectedCourse() {
        int index = courseList.getSelectedIndex();
        if (index >= 0 && index < instructorCourses.size() && !instructorCourses.isEmpty()) {
            return instructorCourses.get(index);
        }
        return null;
    }

    private void handleCreateCourse() {
        String title = JOptionPane.showInputDialog(this, "Enter Course Title:");
        if (title == null || title.trim().isEmpty()) return;

        String desc = JOptionPane.showInputDialog(this, "Enter Course Description:");
        if (desc == null || desc.trim().isEmpty()) return;

        courseService.createCourse(title, desc, currentInstructor.getID())
                .ifPresentOrElse(
                        course -> {
                            JOptionPane.showMessageDialog(this, "Course '" + course.getTitle() + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadCourses();
                        },
                        () -> JOptionPane.showMessageDialog(this, "Failed to create course. Database or ID error.", "Error", JOptionPane.ERROR_MESSAGE)
                );
    }

    private void handleEditCourseDetails() {
        Course course = getSelectedCourse();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newTitle = JOptionPane.showInputDialog(this, "Enter New Title:", course.getTitle());
        if (newTitle == null) return;

        String newDescription = JOptionPane.showInputDialog(this, "Enter New Description:", course.getDescription());
        if (newDescription == null) return;

        if (courseService.editCourseDetails(course.getID(), newTitle, newDescription)) {
            JOptionPane.showMessageDialog(this, "Course details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCourses();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update course details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleManageLessons() {
        Course course = getSelectedCourse();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to manage lessons.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] options = {"Add Lesson", "Delete Lesson", "View Lessons", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Manage Lessons for: " + course.getTitle(),
                "Lesson Management", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            String title = JOptionPane.showInputDialog(this, "Enter Lesson Title:");
            String content = JOptionPane.showInputDialog(this, "Enter Lesson Content:");
            if (title != null && !title.trim().isEmpty() && content != null) {
                if (courseService.addLessonToCourse(course.getID(), title, content)) {
                    JOptionPane.showMessageDialog(this, "Lesson added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add lesson.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (choice == 1) {
            String lessonId = JOptionPane.showInputDialog(this, "Enter Lesson ID to Delete (e.g., C-123-L1):");
            if (lessonId != null && !lessonId.trim().isEmpty()) {
                if (courseService.deleteLessonFromCourse(course.getID(), lessonId)) {
                    JOptionPane.showMessageDialog(this, "Lesson deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete lesson (ID not found or Course error).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (choice == 2) {
            String lessonList = course.getLessons().stream()
                    .map(l -> String.format("ID: %s | Title: %s", l.getID(), l.getTitle()))
                    .collect(Collectors.joining("\n"));

            JTextArea textArea = new JTextArea(lessonList.isEmpty() ? "No lessons in this course." : lessonList);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Current Lessons in " + course.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleViewStudents() {
        Course course = getSelectedCourse();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to view students.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> studentDetails = courseService.getViewEnrolledStudents(course.getID());

        if (studentDetails.isEmpty() || studentDetails.contains("Course not found.")) {
            JOptionPane.showMessageDialog(this, "No students enrolled yet.", "Students", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String studentsString = String.join("\n", studentDetails);
        JTextArea textArea = new JTextArea(studentsString);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Enrolled Students for: " + course.getTitle() + " (" + studentDetails.size() + ")",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
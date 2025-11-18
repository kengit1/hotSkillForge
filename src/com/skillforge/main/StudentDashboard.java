package com.skillforge.main;
import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Lesson;
import com.skillforge.model.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final Student student;
    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;

    private JList<String> availableCoursesList;
    private JList<String> enrolledCoursesList;
    private JList<String> lessonsList;

    public StudentDashboard(Student student, CoursesDatabaseManager courseDB, UserDatabaseManager userDB) {

        this.student = student;
        this.courseDB = courseDB;
        this.userDB = userDB;

        setTitle("Student Dashboard - " + student.getUserName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        loadAvailableCourses();
        loadEnrolledCourses();

        setVisible(true);
    }

    private void initUI() {

        JPanel mainPanel = new JPanel(new GridLayout(1, 3));
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(new JLabel("Available Courses"), BorderLayout.NORTH);
        availableCoursesList = new JList<>();
        availablePanel.add(new JScrollPane(availableCoursesList), BorderLayout.CENTER);

        JButton enrollButton = new JButton("Enroll");
        enrollButton.addActionListener(e -> enrollInCourse());
        availablePanel.add(enrollButton, BorderLayout.SOUTH);
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        enrolledPanel.add(new JLabel("Enrolled Courses"), BorderLayout.NORTH);

        enrolledCoursesList = new JList<>();
        enrolledCoursesList.addListSelectionListener(e -> loadLessons());
        enrolledPanel.add(new JScrollPane(enrolledCoursesList), BorderLayout.CENTER);

        JPanel lessonsPanel = new JPanel(new BorderLayout());
        lessonsPanel.add(new JLabel("Lessons"), BorderLayout.NORTH);

        lessonsList = new JList<>();
        lessonsPanel.add(new JScrollPane(lessonsList), BorderLayout.CENTER);

        JButton markCompleteButton = new JButton("Mark Lesson as Completed");
        markCompleteButton.addActionListener(e -> markLessonCompleted());
        lessonsPanel.add(markCompleteButton, BorderLayout.SOUTH);

        // Add everything
        mainPanel.add(availablePanel);
        mainPanel.add(enrolledPanel);
        mainPanel.add(lessonsPanel);

        add(mainPanel, BorderLayout.CENTER);
    }


    private void loadAvailableCourses() {
        List<Course> allCourses = courseDB.getDataList();

        DefaultListModel<String> model = new DefaultListModel<>();

        for (Course c : allCourses) {
            if (!student.getEnrolledCourses().contains(c.getCourseId())) {
                model.addElement(c.getCourseId() + " - " + c.getTitle());
            }
        }
        availableCoursesList.setModel(model);
    }

    private void loadEnrolledCourses() {

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String courseId : student.getEnrolledCourses()) {
            Course c = courseDB.findById(courseId);
            if (c != null)
                model.addElement(c.getCourseId()+ " - " +c.getTitle());
        }
        enrolledCoursesList.setModel(model);
    }
    private void loadLessons() {

        String selected=enrolledCoursesList.getSelectedValue();
        if (selected==null) return;

        String courseId=selected.split(" - ")[0];
        Course course=courseDB.findById(courseId);

        if (course == null) return;

        DefaultListModel<String> model = new DefaultListModel<>();
        for (Lesson lesson : course.getLessons()) {
            model.addElement(lesson.getLessonId()+ " - "+lesson.getTitle());
        }

        lessonsList.setModel(model);
    }

    private void enrollInCourse() {
        String selected = availableCoursesList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll.");
            return;
        }
        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        if (course == null) return;
        course.addStudent(student.getUserID());
        courseDB.update(course);
        courseDB.saveData();
        student.getEnrolledCourses().add(courseId);
        userDB.update(student);
        userDB.saveData();
        JOptionPane.showMessageDialog(this, "Enrolled successfully!");
        loadAvailableCourses();
        loadEnrolledCourses();
    }
    private void markLessonCompleted() {
        String courseEntry = enrolledCoursesList.getSelectedValue();
        String lessonEntry = lessonsList.getSelectedValue();
        if (courseEntry == null || lessonEntry == null) {
            JOptionPane.showMessageDialog(this, "Select a course and a lesson.");
            return;
        }
        String courseId = courseEntry.split(" - ")[0];
        String lessonId = lessonEntry.split(" - ")[0];
        student.getProgress()
                .computeIfAbsent(courseId, k -> new java.util.ArrayList<>())
                .add(lessonId);
        userDB.update(student);
        userDB.saveData();
        JOptionPane.showMessageDialog(this, "Lesson marked as completed!");
    }
}
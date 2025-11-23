package com.skillforge.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Lesson;
import com.skillforge.model.Instructor;
import com.skillforge.model.QuizEditorDialog;
import com.skillforge.model.Quiz;
import java.util.List;

public class InstructorDashboardFrame extends JFrame {

    private final Instructor instructor;
    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;

    private JList<String> coursesList;
    private JList<String> lessonsList;

    public InstructorDashboardFrame(Instructor instructor, CoursesDatabaseManager courseDB, UserDatabaseManager userDB) {

        this.instructor = instructor;
        this.courseDB = courseDB;
        this.userDB = userDB;

        setTitle("Instructor Dashboard - " + instructor.getUserName());
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        initUI();
        loadCreatedCourses();

        setVisible(true);
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                logoutform logout = new logoutform(instructor,this);
                logout.setVisible(true);
            });
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);

        JButton analyticsBtn = new JButton("View Analytics");
        analyticsBtn.addActionListener(e -> openAnalytics());
        topPanel.add(analyticsBtn, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.add(new JLabel("Your Courses", SwingConstants.CENTER), BorderLayout.NORTH);

        coursesList = new JList<>();
        coursesList.addListSelectionListener(e -> loadLessons());
        coursesPanel.add(new JScrollPane(coursesList), BorderLayout.CENTER);

        JPanel courseButtons = new JPanel(new GridLayout(1, 4));
        JButton createCourseBtn = new JButton("Create Course");
        createCourseBtn.addActionListener(e -> createCourse());
        JButton editCourseBtn = new JButton("Edit Course");
        editCourseBtn.addActionListener(e -> editCourse());
        JButton deleteCourseBtn = new JButton("Delete Course");
        deleteCourseBtn.addActionListener(e -> deleteCourse());
        JButton approveBtn = new JButton("Set Status (P/A/R)");
        approveBtn.addActionListener(e -> changeCourseStatus());
        courseButtons.add(createCourseBtn);
        courseButtons.add(editCourseBtn);
        courseButtons.add(deleteCourseBtn);
        courseButtons.add(approveBtn);
        coursesPanel.add(courseButtons, BorderLayout.SOUTH);

        JPanel lessonsPanel = new JPanel(new BorderLayout());
        lessonsPanel.add(new JLabel("Lessons"), BorderLayout.NORTH);
        lessonsList = new JList<>();
        lessonsPanel.add(new JScrollPane(lessonsList), BorderLayout.CENTER);
        JPanel lessonButtons = new JPanel(new GridLayout(1, 4));
        JButton addLessonBtn = new JButton("Add Lesson");
        addLessonBtn.addActionListener(e -> addLesson());
        JButton editLessonBtn = new JButton("Edit Lesson");
        editLessonBtn.addActionListener(e -> editLesson());
        JButton deleteLessonBtn = new JButton("Delete Lesson");
        deleteLessonBtn.addActionListener(e -> deleteLesson());
        JButton manageQuizBtn = new JButton("Manage Quiz");
        manageQuizBtn.addActionListener(e -> manageQuiz());
        lessonButtons.add(addLessonBtn);
        lessonButtons.add(editLessonBtn);
        lessonButtons.add(deleteLessonBtn);
        lessonButtons.add(manageQuizBtn);
        lessonsPanel.add(lessonButtons, BorderLayout.SOUTH);

        mainPanel.add(coursesPanel);
        mainPanel.add(lessonsPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void openAnalytics() {
        String selected = coursesList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course to view analytics.");
            return;
        }
        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        if (course == null) return;

        InstructorAnalyticsService analyticsService = new InstructorAnalyticsService(courseDB, userDB);
        AnalyticsTabbedFrame frame = new AnalyticsTabbedFrame(this, course, analyticsService);
        frame.setVisible(true);
    }

    private void manageQuiz() {

        String courseSel = coursesList.getSelectedValue();
        String lessonSel = lessonsList.getSelectedValue();

        if (courseSel == null || lessonSel == null) {
            JOptionPane.showMessageDialog(this, "Please select a course and a lesson first.");
            return;
        }

        String courseId = courseSel.split(" - ")[0];
        String lessonId = lessonSel.split(" - ")[0];

        Course course = courseDB.findById(courseId);
        Lesson lesson = course.getLesson(lessonId);

        if (lesson == null) return;

        if (lesson.getQuiz() == null) {
            lesson.setQuiz(new Quiz());
        }

        QuizEditorDialog editor = new QuizEditorDialog(this, lesson.getQuiz());
        editor.setVisible(true);

        courseDB.update(course);
        courseDB.saveData();

        JOptionPane.showMessageDialog(this, "Quiz saved successfully!");
    }

    private void loadCreatedCourses() {
        List<String> created = instructor.getCreatedCourses();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String courseId : created) {
            Course c = courseDB.findById(courseId);
            if (c != null)
                model.addElement(c.getCourseId() + " - " + c.getTitle());
        }
        coursesList.setModel(model);
    }

    private void loadLessons() {
        String selected = coursesList.getSelectedValue();
        if (selected == null) return;
        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);

        if (course == null) return;
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Lesson lesson : course.getLessons()) {
            model.addElement(lesson.getLessonId() + " - " + lesson.getTitle());
        }

        lessonsList.setModel(model);
    }

    private void createCourse() {
        String title = JOptionPane.showInputDialog("Enter Course Title:");
        if (title == null || title.isEmpty()) return;
        String desc = JOptionPane.showInputDialog("Enter Description:");
        if (desc == null || desc.isEmpty()) return;
        String id = "C-" + System.currentTimeMillis();
        Course newCourse = new Course(id, title, desc, instructor.getID());
        newCourse.setApprovalStatus("PENDING"); // when created require approval by admin
        courseDB.add(newCourse);
        courseDB.saveData();
        instructor.addCreatedCourse(id);
        userDB.update(instructor);
        userDB.saveData();
        loadCreatedCourses();
        JOptionPane.showMessageDialog(this, "Course created! Status set to PENDING.");
    }

    private void editCourse() {
        String selected = coursesList.getSelectedValue();
        if (selected == null) return;
        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        String newTitle = JOptionPane.showInputDialog("New Title:", course.getTitle());
        if (newTitle == null || newTitle.isEmpty()) return;
        String newDesc = JOptionPane.showInputDialog("New Description:", course.getDescription());
        if (newDesc == null || newDesc.isEmpty()) return;
        course.setTitle(newTitle);
        course.setDescription(newDesc);
        courseDB.update(course);
        courseDB.saveData();
        loadCreatedCourses();
        JOptionPane.showMessageDialog(this, "Course updated!");
    }

    private void deleteCourse() {
        String selected = coursesList.getSelectedValue();
        if (selected == null) return;
        String courseId = selected.split(" - ")[0];
        instructor.getCreatedCourses().remove(courseId);
        userDB.update(instructor);
        userDB.saveData();
        courseDB.delete(courseId);
        courseDB.saveData();
        loadCreatedCourses();
        JOptionPane.showMessageDialog(this, "Course deleted.");
    }

    private void addLesson() {
        String selected = coursesList.getSelectedValue();
        if (selected == null) return;
        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        String title = JOptionPane.showInputDialog("Lesson Title:");
        String content = JOptionPane.showInputDialog("Lesson Content:");
        if (title == null || content == null) return;
        String lessonId = courseId + "-L" + (course.getLessons().size() + 1);
        Lesson lesson = new Lesson(lessonId, title, content);
        course.addLesson(lesson);
        courseDB.update(course);
        courseDB.saveData();
        loadLessons();
        JOptionPane.showMessageDialog(this, "Lesson added!");
    }

    private void editLesson() {
        String courseSel = coursesList.getSelectedValue();
        String lessonSel = lessonsList.getSelectedValue();
        if (courseSel == null || lessonSel == null) return;
        String courseId = courseSel.split(" - ")[0];
        String lessonId = lessonSel.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        Lesson lesson = course.getLesson(lessonId);
        String newTitle = JOptionPane.showInputDialog("New Lesson Title:", lesson.getTitle());
        String newContent = JOptionPane.showInputDialog("New Lesson Content:", lesson.getContent());
        if (newTitle == null || newContent == null) return;
        lesson.setTitle(newTitle);
        lesson.setContent(newContent);
        courseDB.update(course);
        courseDB.saveData();
        loadLessons();
        JOptionPane.showMessageDialog(this, "Lesson updated!");
    }

    private void deleteLesson() {
        String courseSel = coursesList.getSelectedValue();
        String lessonSel = lessonsList.getSelectedValue();
        if (courseSel == null || lessonSel == null) return;
        String courseId = courseSel.split(" - ")[0];
        String lessonId = lessonSel.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        course.deleteLesson(lessonId);
        courseDB.update(course);
        courseDB.saveData();
        loadLessons();
    }

    private void changeCourseStatus() {
        String selected = coursesList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }
        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);
        if (course == null) return;
        String current = course.getApprovalStatus();
        String newStatus = JOptionPane.showInputDialog(this,
                "Set course status (PENDING / APPROVED / REJECTED):", current == null ? "PENDING" : current);
        if (newStatus == null) return;
        newStatus = newStatus.trim().toUpperCase();
        if (!("PENDING".equals(newStatus) || "APPROVED".equals(newStatus) || "REJECTED".equals(newStatus))) {
            JOptionPane.showMessageDialog(this, "Invalid status. Use PENDING, APPROVED or REJECTED.");
            return;
        }
        course.setApprovalStatus(newStatus);
        courseDB.update(course);
        courseDB.saveData();
        JOptionPane.showMessageDialog(this, "Course status updated to " + newStatus);
        loadCreatedCourses();
    }
}

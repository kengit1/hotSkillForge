package com.skillforge.main;

import com.skillforge.db.CertificateDatabaseManager;
import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Lesson;
import com.skillforge.model.QuizDialog;
import com.skillforge.model.Student;
import com.skillforge.model.Certificate;
import com.skillforge.model.CertificateService;
import java.util.Optional;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final Student student;
    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;
    private final CertificateDatabaseManager certDB;

    private JList<String> availableCoursesList;
    private JList<String> enrolledCoursesList;
    private JList<String> lessonsList;

    private CertificateService certificateService;
    private List<Certificate> certs;
    private JButton getCertificateButton;


    public StudentDashboard(Student student, CoursesDatabaseManager courseDB, UserDatabaseManager userDB,CertificateDatabaseManager certDB) {

        this.student = student;
        this.courseDB = courseDB;
        this.userDB = userDB;
        this.certDB = certDB;

        this.certificateService = new CertificateService(courseDB,userDB,certDB);  // ADD THIS
        this.certs = certificateService.getStudentCertificates(this.student.getUserID());  // NOW SAFE


            setTitle("Student Dashboard - " + student.getUserName());
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        initUI();
        loadAvailableCourses();
        loadEnrolledCourses();

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
                logoutform logout = new logoutform(student,this);
                logout.setVisible(true);
            });
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JButton certificatesBtn = new JButton("My Certificates");
        certificatesBtn.setBackground(new Color(52, 152, 219));
        certificatesBtn.setForeground(Color.WHITE);
        certificatesBtn.setFocusPainted(false);
        certificatesBtn.addActionListener(e -> {
            List<Certificate> certs = certificateService.getStudentCertificates(student.getUserID());

            if (certs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have no certificates yet.");
            } else {
                StringBuilder sb = new StringBuilder("Your Earned Certificates:\n\n");
                for (Certificate c : certs) {
                    sb.append("• ").append(c.getCourseId()).append(" (").append(c.getIssueDate()).append(")\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        });
        topPanel.add(certificatesBtn, BorderLayout.WEST);


        JPanel mainPanel = new JPanel(new GridLayout(1, 3));
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(new JLabel("Available Courses", SwingConstants.CENTER), BorderLayout.NORTH);
        availableCoursesList = new JList<>();
        availablePanel.add(new JScrollPane(availableCoursesList), BorderLayout.CENTER);
        JButton enrollButton = new JButton("Enroll");
        enrollButton.addActionListener(e -> enrollInCourse());
        availablePanel.add(enrollButton, BorderLayout.SOUTH);
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        enrolledPanel.add(new JLabel("Enrolled Courses", SwingConstants.CENTER), BorderLayout.NORTH);
        enrolledCoursesList = new JList<>();
        enrolledCoursesList.addListSelectionListener(e -> loadLessons());
        enrolledPanel.add(new JScrollPane(enrolledCoursesList), BorderLayout.CENTER);

        getCertificateButton = new JButton("Get Certificate");
        getCertificateButton.setVisible(false);

        getCertificateButton.addActionListener(e -> {
            String selectedCourse = enrolledCoursesList.getSelectedValue();
            if (selectedCourse == null) return;
            String courseId = selectedCourse.split(" - ")[0];
            Optional<Certificate> certOpt =
                    certificateService.generateCertificate(student.getUserID(), courseId);
            if (certOpt.isPresent()) {
                Certificate cert = certOpt.get();
                JOptionPane.showMessageDialog(this,
                        "Certificate Generated!\nCertificate ID: " + cert.getCertificateId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "You cannot generate a certificate for this course.\nYou must finish all lessons.",
                        "Not Completed",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        enrolledPanel.add(getCertificateButton, BorderLayout.SOUTH);

        JPanel lessonsPanel = new JPanel(new BorderLayout());
        lessonsPanel.add(new JLabel("Lessons", SwingConstants.CENTER), BorderLayout.NORTH);
        lessonsList = new JList<>();
        lessonsPanel.add(new JScrollPane(lessonsList), BorderLayout.CENTER);

        JPanel lessonButtonsPanel = new JPanel(new GridLayout(2,1,4,4));
        JButton startLessonBtn = new JButton("Open Lesson / Start Quiz");
        startLessonBtn.addActionListener(e -> openOrStartQuiz());
        JButton markCompleteButton = new JButton("Mark Lesson as Completed");
        markCompleteButton.addActionListener(e -> markLessonCompleted());
        lessonButtonsPanel.add(startLessonBtn);
        lessonButtonsPanel.add(markCompleteButton);
        lessonsPanel.add(lessonButtonsPanel, BorderLayout.SOUTH);

        mainPanel.add(availablePanel);
        mainPanel.add(enrolledPanel);
        mainPanel.add(lessonsPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadAvailableCourses() {
        List<Course> allCourses = courseDB.getDataList();

        DefaultListModel<String> model = new DefaultListModel<>();

        for (Course c : allCourses) {
            String status = c.getApprovalStatus();
            if (!student.getEnrolledCourses().contains(c.getCourseId()) && (status == null || "APPROVED".equalsIgnoreCase(status))) {
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

        String selected = enrolledCoursesList.getSelectedValue();
        if (selected == null) {
            lessonsList.setModel(new DefaultListModel<>());
            return;
        }

        String courseId = selected.split(" - ")[0];
        Course course = courseDB.findById(courseId);

        // Toggle certificate button visibility based on completion
        List<String> completed = student.getProgress().getOrDefault(courseId, List.of());
        boolean fullyCompleted = completed.size() == course.getLessons().size();

        getCertificateButton.setVisible(fullyCompleted);

        if (course == null) return;

        DefaultListModel<String> model = new DefaultListModel<>();
        List<String> completedcourses = student.getProgress().getOrDefault(courseId, List.of());

        for (Lesson lesson : course.getLessons()) {
            boolean isCompleted = completed.contains(lesson.getLessonId()) || course.getLessons().stream()
                    .filter(l -> l.getLessonId().equals(lesson.getLessonId()))
                    .anyMatch(l -> l.getCompletedStudents().contains(student.getUserID()));
            String marker = isCompleted ? " (Completed)" : "";
            model.addElement(lesson.getLessonId()+ " - "+lesson.getTitle() + marker);
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
        // Toggle certificate button visibility based on completion
        List<String> completed = student.getProgress().getOrDefault(courseId, List.of());
        boolean fullyCompleted = completed.size() == course.getLessons().size();

        getCertificateButton.setVisible(fullyCompleted);
        if (course == null) return;
        course.addStudent(student.getUserID());
        courseDB.update(course);
        courseDB.saveData();

        if (!student.getEnrolledCourses().contains(courseId)) {
            student.getEnrolledCourses().add(courseId);
        }
        userDB.update(student);
        userDB.saveData();

        JOptionPane.showMessageDialog(this, "Enrolled successfully!");
        loadAvailableCourses();
        loadEnrolledCourses();
    }

    private void openOrStartQuiz() {
        String courseEntry = enrolledCoursesList.getSelectedValue();
        String lessonEntry = lessonsList.getSelectedValue();
        if (courseEntry == null || lessonEntry == null) {
            JOptionPane.showMessageDialog(this, "Select a course and a lesson.");
            return;
        }
        String courseId = courseEntry.split(" - ")[0];
        String lessonId = lessonEntry.split(" - ")[0];

        Course course = courseDB.findById(courseId);
        if (course == null) return;
        Lesson lesson = course.getLesson(lessonId);
        if (lesson == null) return;

        JOptionPane.showMessageDialog(this, "Lesson content:\n\n" + lesson.getContent());
        if (lesson.hasQuiz()) {
            int choice = JOptionPane.showConfirmDialog(this, "This lesson has a quiz. Start now?", "Start Quiz", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                QuizDialog quizDialog = new QuizDialog(this, lesson.getTitle(), lesson.getQuiz());
                quizDialog.setVisible(true);

                if (quizDialog.isPassed()) {
                    double score = quizDialog.getScore();
                    student.addQuizScore(lessonId, score);
                    student.addCompletedLesson(courseId, lessonId);
                    lesson.addQuizResult(student.getUserID(), score);
                    lesson.addCompletedStudent(student.getUserID());
                    userDB.update(student);
                    userDB.saveData();

                    courseDB.update(course);
                    courseDB.saveData();

                    JOptionPane.showMessageDialog(this, "Quiz passed! Score saved and lesson completed.");
                    loadLessons();
                } else {
                    JOptionPane.showMessageDialog(this, "You did not pass the quiz. Lesson not completed.");
                }
            }
        }
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

        Course course = courseDB.findById(courseId);
        if (course == null) return;
        Lesson lesson = course.getLesson(lessonId);
        if (lesson == null) return;

        // check duplicate completion
        List<String> completed = student.getProgress().computeIfAbsent(courseId, k -> new ArrayList<>());
        if (completed.contains(lessonId) || lesson.getCompletedStudents().contains(student.getUserID())) {
            JOptionPane.showMessageDialog(this, "You already completed this lesson.");
            return;
        }

        if (lesson.hasQuiz()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "This lesson has a quiz. You must pass it to complete the lesson.\nStart Quiz now?",
                    "Quiz Required", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                QuizDialog quizDialog = new QuizDialog(this, lesson.getTitle(), lesson.getQuiz());
                quizDialog.setVisible(true);

                if (quizDialog.isPassed()) {
                    double score = quizDialog.getScore();
                    student.addQuizScore(lessonId, score);
                    student.addCompletedLesson(courseId, lessonId);

                    lesson.addQuizResult(student.getUserID(), score);
                    lesson.addCompletedStudent(student.getUserID());

                    userDB.update(student);
                    userDB.saveData();

                    courseDB.update(course);
                    courseDB.saveData();

                    JOptionPane.showMessageDialog(this, "Lesson Completed & Score Saved!");
                    loadLessons();
                } else {
                    JOptionPane.showMessageDialog(this, "You did not pass the quiz. Lesson not completed.");
                }
            }
        } else {
            student.addCompletedLesson(courseId, lessonId);
            lesson.addCompletedStudent(student.getUserID());

            userDB.update(student);
            userDB.saveData();

            courseDB.update(course);
            courseDB.saveData();

            JOptionPane.showMessageDialog(this, "Lesson marked as completed!");
            loadLessons();
        }
    }
    public boolean hasCompletedCourse(Student student, Course course) {
        List<String> completedLessons = student.getProgress().get(course.getID());
        if (completedLessons == null) return false;

        return completedLessons.size() == course.getLessons().size();
    }

}

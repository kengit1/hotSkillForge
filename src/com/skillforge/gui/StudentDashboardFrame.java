import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Lesson;
import com.skillforge.model.Student;
import com.skillforge.model.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.gson.internal.bind.TypeAdapters.UUID;

public static class StudentDashboardFrame extends JFrame {
    private Student currentStudent;
    private UserDatabaseManager ud;
    private CoursesDatabaseManager cd;

    private JTabbedPane tabbedPane;
    private JPanel availableCoursesPanel;
    private JPanel enrolledCoursesPanel;


    public StudentDashboardFrame(Student student, UserDatabaseManager userManager, CoursesDatabaseManager courseManager) {
        this.currentStudent = student;
        this.ud = userManager;
        this.cd = courseManager;

        setTitle("Skill Forge - Student Dashboard: " + student.getUserName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLayout(new BorderLayout(15, 15));
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        availableCoursesPanel = new JPanel();
        availableCoursesPanel.setLayout(new BoxLayout(availableCoursesPanel, BoxLayout.Y_AXIS));
        tabbedPane.addTab("Browse Available Courses", new JScrollPane(availableCoursesPanel));

        enrolledCoursesPanel = new JPanel();
        enrolledCoursesPanel.setLayout(new BoxLayout(enrolledCoursesPanel, BoxLayout.Y_AXIS));
        tabbedPane.addTab("My Enrolled Courses", new JScrollPane(enrolledCoursesPanel));

        add(tabbedPane, BorderLayout.CENTER);
        loadDataViews();
        setLocationRelativeTo(null);
    }

    private void loadDataViews() {
        loadAvailableCourses();
        loadEnrolledCourses();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue background

        JLabel welcomeLabel = new JLabel("Welcome, " + currentStudent.getUserName() + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(255, 99, 71)); // Tomato red
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        logoutButton.addActionListener(e -> {
            // Save data and exit/return to login
            ud.saveData();
            cd.saveData();
            JOptionPane.showMessageDialog(this, "Logged out. All data saved successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        panel.add(logoutButton, BorderLayout.EAST);
        return panel;
    }
    private void loadAvailableCourses() {
        availableCoursesPanel.removeAll();
        availableCoursesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Course> allCourses = cd.getDataList();
        List<Course> availableCourses = allCourses.stream().filter(course -> !currentStudent.getEnrolledCourses().contains(course.getCourseId())).collect(Collectors.toList());

        if (availableCourses.isEmpty()) {
            availableCoursesPanel.add(new JLabel("No new courses available. Check back later!"));
        } else {
            for (Course course : availableCourses) {
                availableCoursesPanel.add(createCoursePanel(course, false));
                availableCoursesPanel.add(Box.createVerticalStrut(10)); // Spacer
            }
        }
        availableCoursesPanel.revalidate();
        availableCoursesPanel.repaint();
    }

    private void loadEnrolledCourses() {
        enrolledCoursesPanel.removeAll();
        enrolledCoursesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (currentStudent.getEnrolledCourses()==null) {
            enrolledCoursesPanel.add(new JLabel("You are not currently enrolled in any courses. Enroll now to start learning!"));
        } else {
            for (String courseId : currentStudent.getEnrolledCourses()) {
                Course course = cd.findById(courseId);
                if (course != null) {
                    enrolledCoursesPanel.add(createCoursePanel(course, true));
                    enrolledCoursesPanel.add(Box.createVerticalStrut(10));
                } else {
                    enrolledCoursesPanel.add(new JLabel("Course ID " + courseId + " not found (Deleted)."));
                }
            }
        }
        enrolledCoursesPanel.revalidate();
        enrolledCoursesPanel.repaint();
    }

    private JPanel createCoursePanel(Course course, boolean isEnrolled) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Left side: Title and Description
        JTextArea info = new JTextArea(course.getTitle() +
                "\nInstructor ID: " + course.getInstructorId() +
                "\n\n" + course.getDescription());
        info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.setEditable(false);
        info.setWrapStyleWord(true);
        info.setLineWrap(true);
        info.setOpaque(false);
        panel.add(info, BorderLayout.CENTER);

        // Right side: Action Button
        JButton actionButton;
        if (isEnrolled) {
            actionButton = new JButton("Access Lessons");
            actionButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
            actionButton.setForeground(Color.WHITE);
            actionButton.addActionListener(e -> showLessonView(course));
        } else {
            actionButton = new JButton("Enroll Now");
            actionButton.setBackground(new Color(30, 144, 255)); // Dodger Blue
            actionButton.setForeground(Color.WHITE);
            actionButton.addActionListener(e -> enrollCourse(course));
        }
        actionButton.setFocusPainted(false);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.add(actionButton);
        buttonWrapper.setOpaque(false);
        panel.add(buttonWrapper, BorderLayout.EAST);

        panel.setMaximumSize(new Dimension(800, 180));
        return panel;
    }



    private void enrollCourse(Course course) {
        if (currentStudent.getEnrolledCourses().contains(course.getID())) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentStudent.enrollCourse(course);
        course.addStudentId(currentStudent.getUserID());
        ud.update(currentStudent);
        cd.update(course);
        JOptionPane.showMessageDialog(this, "Successfully enrolled in " + course.getTitle() + "! Data saved at logout.", "Enrollment Success", JOptionPane.INFORMATION_MESSAGE);
        loadDataViews();
        tabbedPane.setSelectedIndex(1);
    }

    private void showLessonView(Course course) {

        new com.skillforge.ui.LessonFrame(this, currentStudent, course, ud).setVisible(true);
        loadDataViews();
    }
}

public static void main(String[] args) {
   Student k=new Student("9666","OMar Hesham","omarhesham@kokowawa.com","omarhesham1bas");
   UserDatabaseManager ds=new UserDatabaseManager("users.json");
   CoursesDatabaseManager cf=new CoursesDatabaseManager();
  StudentDashboardFrame s=new StudentDashboardFrame(k,ds,cf);




}




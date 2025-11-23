package com.skillforge.main;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Admin;
import com.skillforge.model.Course;
import com.skillforge.model.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;
    private final CourseService courseService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton refreshButton;

    public AdminDashboard(Admin admin) {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        courseDB = new CoursesDatabaseManager();
        userDB = new UserDatabaseManager("users.json");
        this.courseService = new CourseService(courseDB, userDB);

        tableModel = new DefaultTableModel(new Object[]{"Course ID", "Title", "Instructor ID", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        JPanel topPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                logoutform logout = new logoutform(admin,this);
                logout.setVisible(true);
            });
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        add(scroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> approveSelectedCourse());
        rejectButton.addActionListener(e -> rejectSelectedCourse());
        refreshButton.addActionListener(e -> loadPendingCourses());

        loadPendingCourses();
    }

    private void loadPendingCourses() {
        tableModel.setRowCount(0);
        List<Course> pending = courseDB.getPendingCourses();
        for (Course c : pending) {
            String instructorId = c.getInstructorId();
            String status = c.getApprovalStatus();
            tableModel.addRow(new Object[]{c.getCourseId(), c.getTitle(), instructorId, status});
        }
    }

    private void approveSelectedCourse() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to approve.");
            return;
        }
        String courseId = (String) tableModel.getValueAt(row, 0);
        boolean ok = courseService.approveCourse(courseId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Course approved.");
            loadPendingCourses();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to approve course.");
        }
    }

    private void rejectSelectedCourse() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to reject.");
            return;
        }
        String courseId = (String) tableModel.getValueAt(row, 0);
        boolean ok = courseService.rejectCourse(courseId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Course rejected.");
            loadPendingCourses();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject course.");
        }
    }
}

package com.skillforge.main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.google.gson.JsonObject;
import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skillforge.Utilities.*;
import com.skillforge.model.*;

public class login_panel extends JFrame{
    private JPanel container2;
    private JTextField emailfield;
    private JPasswordField passwordField1;
    private JButton loginButton;
    private JComboBox comboBox1;
    private JButton backButton;

    public login_panel() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setContentPane(container2);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                login_operations();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                goback();
                dispose();
            }
        });
    }

    private void login_operations(){
            String type = (String) comboBox1.getSelectedItem();
            String email = emailfield.getText().trim();
            String password = new String(passwordField1.getPassword());

            File file = new File("users.json");
            if (!file.exists()) {
                System.out.println("ERROR: users.json file does not exist.");
                return;
            }
            try {
                Student S = null;
                Instructor I = null;
                String content = new String(Files.readAllBytes(file.toPath()));
                if (content.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No users found!", "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JSONArray usersArray = new JSONArray(content);
                boolean found = false;
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userObj = usersArray.getJSONObject(i);
                    String userID = userObj.getString("userID");
                    String username = userObj.getString("userName");
                    String userEmail = userObj.getString("email");
                    String userRole = userObj.getString("role");
                    String userPasswordHash = userObj.getString("passwordHash");
                    JSONArray EnrolledCoursesArray;
                    JSONArray CreatedcoursesArray;
                    JSONObject progressObj;

                    if (true){
                        /*userEmail.equals(email) && userRole.equals(type) & userPasswordHash.equals(securityUtils.hashPassword(password))*/
                        found = true;
                        if(userRole.equals("Student")){
                            EnrolledCoursesArray = userObj.getJSONArray("enrolledCourses");
                            progressObj = userObj.getJSONObject("progress");
                            List<String> EnrolledCourses = getCoursesList(EnrolledCoursesArray);
                            Map<String, List<String>> progress = getProgressMap(progressObj);
                            S = new Student(userID,userRole,username,userEmail,userPasswordHash,EnrolledCourses,progress);
                        }
                        else if(userRole.equals("Instructor")){
                            CreatedcoursesArray = userObj.getJSONArray("createdCourses");
                            List<String> CreatedCourses = getCoursesList(CreatedcoursesArray);
                            I = new Instructor(userID,"Instructor",username,userEmail,userPasswordHash,CreatedCourses);
                        }
                        break;
                    }
                }
                if (found) {
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    emailfield.setText("");
                    passwordField1.setText("");
                    if(type.equals("Student")) openStudentDashboard(S);
                    else if(type.equals("Instructor")) openInstructorDashborad(I);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or user type!", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println("ERROR: Failed to read users.json");
            }
        }

    private List<String> getCoursesList(JSONArray array){
        List<String> Courses = new ArrayList<>();
        for (int j = 0; j < array.length(); j++) {
            Courses.add(array.getString(j));
        }
        return Courses;
    }

    private Map<String, List<String>> getProgressMap(JSONObject object){
        Map<String, List<String>> progress = new HashMap<>();
        for (String course : object.keySet()) {
            JSONArray lessonsArray = object.getJSONArray(course);
            List<String> lessons = new ArrayList<>();
            for (int j = 0; j < lessonsArray.length(); j++) {
                lessons.add(lessonsArray.getString(j));
            }
            progress.put(course, lessons);
        }
        return progress;
    }

    private void goback() {

        SwingUtilities.invokeLater(() -> {
            first_panel back = new first_panel();
            back.setVisible(true);
        });
    }

    private void openStudentDashboard(Student s){
        UserDatabaseManager us=new UserDatabaseManager("users.json");
        CoursesDatabaseManager cs=new CoursesDatabaseManager();
        SwingUtilities.invokeLater(() -> {
            StudentDashboard panel = new StudentDashboard(s,cs,us);
            panel.setVisible(true);
        });
    }
    private void openInstructorDashborad(Instructor i){
        UserDatabaseManager us=new UserDatabaseManager("users.json");
        CoursesDatabaseManager cs=new CoursesDatabaseManager();
        SwingUtilities.invokeLater(() -> {
            InstructorDashboardFrame panel = new InstructorDashboardFrame(i,cs,us);
            panel.setVisible(true);
        });
    }
}

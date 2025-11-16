package com.skillforge.gui;

import com.skillforge.Utilities.securityUtils;
import com.skillforge.model.Student;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

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
                String content = new String(Files.readAllBytes(file.toPath()));
                if (content.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No users found!", "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JSONArray usersArray = new JSONArray(content);
                boolean found = false;
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userObj = usersArray.getJSONObject(i);
                    String userEmail = userObj.getString("email");
                    String userRole = userObj.getString("role");
                    String userPasswordHash = userObj.getString("passwordHash");
                    if (userEmail.equals(email) && userRole.equals(type) && userPasswordHash.equals(securityUtils.hashPassword(password))) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or user type!", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println("ERROR: Failed to read users.json");
            }
        }

    private void goback() {

        SwingUtilities.invokeLater(() -> {
            first_panel back = new first_panel();
            back.setVisible(true);
        });
    }
}

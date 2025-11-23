package com.skillforge.main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.skillforge.model.*;
import com.skillforge.Utilities.*;
import com.skillforge.db.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class signup_panel extends JFrame {
    private JPanel container3;
    private JComboBox comboBox1;
    private JTextField Emailfield;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JButton signupButton;
    private JTextField namefield;
    private JButton backButton;

    public signup_panel() {
        setTitle("Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setContentPane(container3);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Signup_operations();
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

    private void Signup_operations() {
        String Usertype = (String) comboBox1.getSelectedItem();
        String Username = namefield.getText().trim();
        String Email = Emailfield.getText().trim();
        String password1 = new String(passwordField1.getPassword());
        String password2 = new String(passwordField2.getPassword());

        if (Usertype.isEmpty() || Username.isEmpty() || Email.isEmpty() || password1.isEmpty() || password2.isEmpty())
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Input Error", JOptionPane.ERROR_MESSAGE);

        else if (!ValidName(Username))
            JOptionPane.showMessageDialog(this, "Invalid Username", "Input Error", JOptionPane.ERROR_MESSAGE);

        else if (!ValidEmail(Email))
            JOptionPane.showMessageDialog(this, "Invalid Email Format", "Input Error", JOptionPane.ERROR_MESSAGE);

        else if (!ValidPassword(password1))
            JOptionPane.showMessageDialog(this, "Invalid Password", "Input Error", JOptionPane.ERROR_MESSAGE);

        else if (!password1.equals(password2))
            JOptionPane.showMessageDialog(this, "Passwords don't match", "Input Error", JOptionPane.ERROR_MESSAGE);
        else {
            String ID = IDgenerator();
            if (Usertype.equals("Student")) {
                User newUser = new Student(
                        ID,
                        "Student",
                        Username,
                        Email,
                        securityUtils.hashPassword(password1),
                        null,
                        null,
                        null
                );
                UserDatabaseManager db = new UserDatabaseManager("users.json");
                db.add(newUser);
                db.saveData();
                JOptionPane.showMessageDialog(this, "Successful signup", "Success", JOptionPane.INFORMATION_MESSAGE);
                namefield.setText("");
                passwordField1.setText("");
                passwordField2.setText("");
                Emailfield.setText("");
                openloginpanel();
                dispose();
            } else if (Usertype.equals("Instructor")) {
                User newUser = new Instructor(
                        ID,
                        "Instructor",
                        Username,
                        Email,
                        securityUtils.hashPassword(password1),
                        null
                );
                UserDatabaseManager db = new UserDatabaseManager("users.json");
                db.add(newUser);
                db.saveData();
                JOptionPane.showMessageDialog(this, "Successful signup", "Success", JOptionPane.INFORMATION_MESSAGE);
                namefield.setText("");
                passwordField1.setText("");
                passwordField2.setText("");
                Emailfield.setText("");
                openloginpanel();
                dispose();
            }
            else if(Usertype.equals("Admin")){
                User newUser = new Admin(
                        ID,
                        Username,
                        Email,
                        securityUtils.hashPassword(password1)
                );
                UserDatabaseManager db = new UserDatabaseManager("users.json");
                db.add(newUser);
                db.saveData();
                JOptionPane.showMessageDialog(this, "Successful signup", "Success", JOptionPane.INFORMATION_MESSAGE);
                namefield.setText("");
                passwordField1.setText("");
                passwordField2.setText("");
                Emailfield.setText("");
                openloginpanel();
                dispose();
            }
        }
    }

    private boolean ValidName(String name) {
        if (name.matches("[a-zA-Z]+") && name.length() <= 15) return true;
        else return false;
    }

    private boolean ValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$";
        return email.matches(emailRegex);
    }

    private boolean ValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasNonLetter = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else {
                hasNonLetter = true;
            }
            if (hasLetter && hasNonLetter) {
                return true;
            }
        }
        return hasLetter && hasNonLetter;
    }

    private String IDgenerator() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("users.json")));
            JSONArray usersArray = new JSONArray(content);
            Set<String> existingIDs = new HashSet<>();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObj = usersArray.getJSONObject(i);
                existingIDs.add(userObj.getString("userID"));
            }
            Random random = new Random();
            String newID;
            do {
                int number = random.nextInt(9000) + 1000; // ensures 1000–9999
                newID = String.valueOf(number);
            } while (existingIDs.contains(newID));

            return newID;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void goback() {

        SwingUtilities.invokeLater(() -> {
            first_panel back = new first_panel();
            back.setVisible(true);
        });
    }

    private void openloginpanel() {

        SwingUtilities.invokeLater(() -> {
            login_panel login = new login_panel();
            login.setVisible(true);
        });
    }
}

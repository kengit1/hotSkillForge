package com.skillforge.gui;
import com.skillforge.model.*;
import com.skillforge.Utilities.securityUtils;
import com.skillforge.model.Instructor;
import com.skillforge.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import com.skillforge.model.Student;
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
            if (Usertype.equals("Student")) {
                User newUser;
                newUser = new Student("1234","Student", Username, Email, securityUtils.hashPassword(password1),null,null);
                saveUserToJSON(newUser);
            } else if (Usertype.equals("Instructor")) {
                User newUser = new Instructor(
                        "5678",
                        Username,
                        Email,
                        securityUtils.hashPassword(password1)
                );
                saveUserToJSON(newUser);
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

    private void saveUserToJSON(User user) {
        File file = new File("users.json");
        if (!file.exists()) {
            System.out.println("ERROR: users.json file does not exist. User data was NOT saved.");
            return;
        }
        JSONArray usersArray;
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            if (content.trim().isEmpty()) {
                usersArray = new JSONArray();
            } else {
                usersArray = new JSONArray(content);
            }
            JSONObject userObj = new JSONObject();
            userObj.put("userID", user.getUserID());
            userObj.put("userName", user.getUserName());
            userObj.put("role", user.getRole());
            userObj.put("email", user.getEmail());
            userObj.put("passwordHash", user.getPasswordHash());
            usersArray.put(userObj);
            FileWriter writer = new FileWriter(file);
            writer.write(usersArray.toString(4));
            writer.close();

            JOptionPane.showMessageDialog(this, "Successful signup", "Success", JOptionPane.INFORMATION_MESSAGE);
            namefield.setText("");
            passwordField1.setText("");
            passwordField2.setText("");
            Emailfield.setText("");
            openloginpanel();
            dispose();

        } catch (Exception e) {
            System.out.println("ERROR: Failed to read or write users.json");
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

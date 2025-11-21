package com.skillforge.main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class first_panel extends JFrame {
    private JPanel container1;
    private JButton loginButton;
    private JButton signupButton;

    public first_panel() {
        setTitle("FirstPanel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setContentPane(container1);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openloginpanel();
                dispose();
            }
        });
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                opensignuppanel();
                dispose();
            }
        });
    }

    private void openloginpanel() {

        SwingUtilities.invokeLater(() -> {
            login_panel login = new login_panel();
            login.setVisible(true);
        });
    }
    private void opensignuppanel() {

        SwingUtilities.invokeLater(() -> {
            signup_panel signup = new signup_panel();
            signup.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            first_panel panel = new first_panel();
            panel.setVisible(true);
        });
    }
}

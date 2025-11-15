package com.skillforge.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class login_panel extends JFrame{
    private JPanel container2;
    private JTextField emailfield;
    private JPasswordField passwordField1;
    private JButton loginButton;
    private JComboBox comboBox1;

    public login_panel() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setContentPane(container2);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            login_panel panel = new login_panel();
            panel.setVisible(true);
        });
    }
}

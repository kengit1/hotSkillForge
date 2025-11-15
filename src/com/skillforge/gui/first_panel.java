package com.skillforge.gui;

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
        setContentPane(container1);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            first_panel panel = new first_panel();
            panel.setVisible(true);
        });
    }
}

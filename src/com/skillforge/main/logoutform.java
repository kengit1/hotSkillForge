package com.skillforge.main;

import com.skillforge.model.User;
import com.skillforge.Utilities.securityUtils;
import javax.swing.*;

public class logoutform extends JFrame {
    private JPasswordField passwordField1;
    private JPanel container4;
    private JButton EnterButton;

    private final JFrame parentWindow;  // << reference to dashboard

    public logoutform(User u, JFrame parentWindow) {
        this.parentWindow = parentWindow;

        setTitle("logout");
        setSize(250, 100);
        setLocationRelativeTo(null);
        setContentPane(container4);

        EnterButton.addActionListener(e -> logoutoperations(u));
    }

    private void logoutoperations(User u) {
        String password = new String(passwordField1.getPassword());
        if (securityUtils.hashPassword(password).equals(u.getPasswordHash())) {
            parentWindow.dispose();
            dispose();
            openfirstpanel();
        } else {
            JOptionPane.showMessageDialog(this, "Wrong Password", "Input Error", JOptionPane.ERROR_MESSAGE);
            passwordField1.setText("");
        }
    }
    private void openfirstpanel() {
        SwingUtilities.invokeLater(() -> {
            first_panel panel = new first_panel();
            panel.setVisible(true);
        });
    }
}


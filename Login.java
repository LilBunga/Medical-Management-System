package com.project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Login implements ActionListener {
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin";

    private JFrame frame;
    private JTextField usernameTf;
    private JPasswordField passwordTf;
    private JButton submit, back;

    Login() {
        frame = new JFrame("Medical Management System — Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        // Full-screen background
        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), getHeight(), new Color(30, 58, 138)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Login card
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(UITheme.CARD_BG);
        card.setPreferredSize(new Dimension(420, 500));
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));

        // Card top accent
        JPanel accent = new JPanel();
        accent.setBackground(UITheme.PRIMARY);
        accent.setPreferredSize(new Dimension(0, 5));
        card.add(accent, BorderLayout.NORTH);

        // Card content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.CARD_BG);
        content.setBorder(new EmptyBorder(36, 40, 36, 40));

        // Logo / title area
        JLabel appTitle = new JLabel("MedManage");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appTitle.setForeground(UITheme.PRIMARY);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Medical Management System");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel signInLabel = new JLabel("Sign in to your account");
        signInLabel.setFont(UITheme.FONT_H2);
        signInLabel.setForeground(UITheme.TEXT_PRIMARY);
        signInLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form fields
        JLabel userLabel = UITheme.createFormLabel("Username");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameTf = UITheme.createTextField();
        usernameTf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameTf.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = UITheme.createFormLabel("Password");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordTf = new JPasswordField();
        passwordTf.setFont(UITheme.FONT_BODY);
        passwordTf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(7, 10, 7, 10)
        ));
        passwordTf.setBackground(Color.WHITE);
        passwordTf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordTf.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Enter key submits
        passwordTf.addActionListener(this);
        usernameTf.addActionListener(this);

        submit = UITheme.createPrimaryButton("Sign In");
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.addActionListener(this);

        back = UITheme.createButton("Back to Welcome", new Color(100, 116, 139), new Color(71, 85, 105));
        back.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(this);

        content.add(appTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(24));
        content.add(sep);
        content.add(Box.createVerticalStrut(24));
        content.add(signInLabel);
        content.add(Box.createVerticalStrut(24));
        content.add(userLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(usernameTf);
        content.add(Box.createVerticalStrut(16));
        content.add(passLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(passwordTf);
        content.add(Box.createVerticalStrut(24));
        content.add(submit);
        content.add(Box.createVerticalStrut(10));
        content.add(back);

        card.add(content, BorderLayout.CENTER);

        // Footer inside bg
        JLabel footer = new JLabel("Final Project — Medical Management System");
        footer.setFont(UITheme.FONT_SMALL);
        footer.setForeground(new Color(100, 116, 139));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cardWrapper = new JPanel();
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
        cardWrapper.setOpaque(false);
        cardWrapper.add(card);
        cardWrapper.add(Box.createVerticalStrut(16));
        cardWrapper.add(footer);

        bg.add(cardWrapper);
        frame.add(bg, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            new Welcome();
            frame.dispose();
            return;
        }

        String enteredUser = usernameTf.getText().trim();
        String enteredPass = new String(passwordTf.getPassword());

        if (enteredUser.isEmpty() || enteredPass.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both username and password.", "Login", JOptionPane.WARNING_MESSAGE);
        } else if (enteredUser.equals(VALID_USERNAME) && enteredPass.equals(VALID_PASSWORD)) {
            new Admin_GUI();
            frame.dispose();
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordTf.setText("");
        }
    }
}

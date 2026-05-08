package com.project;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class Admin_GUI implements ActionListener {
    static JPanel center, west, north;
    static JFrame frame;

    private JButton logout, drugs, company, warning, sales;

    Admin_GUI() {
        frame = new JFrame("Medical Management System");
        frame.setLayout(new BorderLayout(0, 0));

        // === HEADER ===
        north = new JPanel(new BorderLayout());
        north.setBackground(UITheme.CARD_BG);
        north.setPreferredSize(new Dimension(0, 68));
        north.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JLabel title = new JLabel("  Medical Management System");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        headerRight.setBackground(UITheme.CARD_BG);
        headerRight.setBorder(new EmptyBorder(0, 0, 0, 16));

        JLabel userLabel = new JLabel("Admin");
        userLabel.setFont(UITheme.FONT_LABEL);
        userLabel.setForeground(UITheme.TEXT_MUTED);

        JButton aboutBtn = UITheme.createButton("About Us", UITheme.PRIMARY, UITheme.PRIMARY_HOVER);
        aboutBtn.addActionListener(e -> showAboutUs());

        headerRight.add(userLabel);
        headerRight.add(aboutBtn);

        north.add(title, BorderLayout.CENTER);
        north.add(headerRight, BorderLayout.EAST);

        // === SIDEBAR ===
        west = new JPanel();
        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
        west.setBackground(UITheme.SIDEBAR_BG);
        west.setPreferredSize(new Dimension(220, 0));

        // Brand
        JPanel brand = new JPanel(new BorderLayout());
        brand.setBackground(UITheme.SIDEBAR_BRAND);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        brand.setPreferredSize(new Dimension(220, 68));
        brand.setBorder(new EmptyBorder(0, 24, 0, 0));

        JLabel brandName = new JLabel("MedManage");
        brandName.setFont(UITheme.FONT_BRAND);
        brandName.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("  v1.0 — Pharmacy System");
        brandSub.setFont(UITheme.FONT_SMALL);
        brandSub.setForeground(UITheme.SIDEBAR_TEXT);

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setBackground(UITheme.SIDEBAR_BRAND);
        brandText.add(Box.createVerticalGlue());
        brandText.add(brandName);
        brandText.add(brandSub);
        brandText.add(Box.createVerticalGlue());
        brand.add(brandText, BorderLayout.CENTER);

        // Nav separator label
        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setFont(UITheme.FONT_SMALL);
        navLabel.setForeground(new Color(71, 85, 105));
        navLabel.setBorder(new EmptyBorder(18, 28, 6, 0));
        navLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        drugs   = UITheme.createNavButton("\u25a3", "Drugs");
        company = UITheme.createNavButton("\u25a3", "Company");
        sales   = UITheme.createNavButton("\u25a3", "Sales");
        warning = UITheme.createNavButton("\u25a3", "Warning");

        drugs.addActionListener(this);
        company.addActionListener(this);
        sales.addActionListener(this);
        warning.addActionListener(this);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.SIDEBAR_ITEM);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Logout at bottom
        logout = UITheme.createNavButton("\u2192", "Logout");
        logout.addActionListener(this);

        west.add(brand);
        west.add(navLabel);
        west.add(drugs);
        west.add(company);
        west.add(sales);
        west.add(warning);
        west.add(Box.createVerticalGlue());
        west.add(sep);
        west.add(logout);
        west.add(Box.createVerticalStrut(10));

        // === CENTER ===
        center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.CONTENT_BG);
        showDashboard();

        // === FRAME ===
        frame.add(north, BorderLayout.NORTH);
        frame.add(west, BorderLayout.WEST);
        frame.add(center, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(1000, 600));
        frame.setVisible(true);
    }

    private void showDashboard() {
        center.removeAll();

        JPanel dashboard = new JPanel(new BorderLayout(0, 0));
        dashboard.setBackground(UITheme.CONTENT_BG);

        // Dashboard header
        JPanel header = UITheme.createSectionHeader("Dashboard");
        dashboard.add(header, BorderLayout.NORTH);

        // Stat cards
        JPanel statsArea = new JPanel(new GridLayout(1, 3, 20, 0));
        statsArea.setBackground(UITheme.CONTENT_BG);
        statsArea.setBorder(new EmptyBorder(24, 24, 24, 24));

        String drugCount = "—", companyCount = "—", salesCount = "—";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM mm_drugs")) {
                if (rs.next()) drugCount = rs.getString(1);
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM mm_company")) {
                if (rs.next()) companyCount = rs.getString(1);
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM mm_sales")) {
                if (rs.next()) salesCount = rs.getString(1);
            }
        } catch (Exception ignored) {}

        statsArea.add(UITheme.createStatCard("Total Drugs", drugCount, UITheme.PRIMARY));
        statsArea.add(UITheme.createStatCard("Companies", companyCount, UITheme.SUCCESS));
        statsArea.add(UITheme.createStatCard("Sales Records", salesCount, UITheme.WARNING_COLOR));

        // Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(UITheme.CONTENT_BG);
        welcomePanel.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel welcomeTitle = new JLabel("Welcome to Medical Management System");
        welcomeTitle.setFont(UITheme.FONT_TITLE);
        welcomeTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel welcomeDesc = new JLabel("<html><br>Use the sidebar navigation to manage drugs, companies, sales, and view stock warnings.<br>" +
                "Select a menu to get started.</html>");
        welcomeDesc.setFont(UITheme.FONT_BODY);
        welcomeDesc.setForeground(UITheme.TEXT_MUTED);

        card.add(welcomeTitle, BorderLayout.NORTH);
        card.add(welcomeDesc, BorderLayout.CENTER);

        welcomePanel.add(card);

        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(UITheme.CONTENT_BG);
        content.add(statsArea, BorderLayout.NORTH);
        content.add(welcomePanel, BorderLayout.CENTER);

        dashboard.add(content, BorderLayout.CENTER);

        center.add(dashboard, BorderLayout.CENTER);
        center.revalidate();
        center.repaint();
    }

    private void showAboutUs() {
        JDialog dialog = new JDialog(frame, "About Us", true);
        dialog.setSize(700, 280);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        JPanel header = UITheme.createSectionHeader("Development Team");
        dialog.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 3, 16, 0));
        content.setBackground(UITheme.CONTENT_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[][] members = {
            {"Kakde Shantanu", "Roll No. 25"},
            {"Kale Atharva",   "Roll No. 26"},
            {"Patil Chetan",   "Roll No. 66"}
        };

        for (String[] m : members) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(UITheme.CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.PRIMARY),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER, 1),
                    new EmptyBorder(16, 16, 16, 16)
                )
            ));

            JLabel name = new JLabel(m[0]);
            name.setFont(UITheme.FONT_H2);
            name.setForeground(UITheme.TEXT_PRIMARY);
            name.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel roll = new JLabel(m[1]);
            roll.setFont(UITheme.FONT_BODY);
            roll.setForeground(UITheme.TEXT_MUTED);
            roll.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(name);
            card.add(Box.createVerticalStrut(6));
            card.add(roll);
            content.add(card);
        }

        JPanel btnPanel = UITheme.createDialogButtonPanel(
            UITheme.createPrimaryButton("Close")
        );
        ((JButton) btnPanel.getComponent(0)).addActionListener(e -> dialog.dispose());

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void showLogoutDialog() {
        JDialog dialog = new JDialog(frame, "Logout", true);
        dialog.setSize(380, 180);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.CARD_BG);
        content.setBorder(new EmptyBorder(28, 28, 20, 28));

        JLabel msg = new JLabel("Are you sure you want to logout?");
        msg.setFont(UITheme.FONT_H2);
        msg.setForeground(UITheme.TEXT_PRIMARY);
        content.add(msg, BorderLayout.CENTER);

        JButton yes = UITheme.createDangerButton("Yes, Logout");
        JButton no  = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        yes.addActionListener(e -> frame.dispose());
        no.addActionListener(e -> dialog.dispose());

        JPanel btns = UITheme.createDialogButtonPanel(no, yes);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == drugs) {
            new Admin_GUI_drugs();
        } else if (e.getSource() == company) {
            new Admin_GUI_company();
        } else if (e.getSource() == sales) {
            new Admin_GUI_sales();
        } else if (e.getSource() == warning) {
            new Admin_GUI_warning();
        } else if (e.getSource() == logout) {
            showLogoutDialog();
        }
    }
}

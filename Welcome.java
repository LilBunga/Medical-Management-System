package com.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Welcome implements ActionListener {
    private JFrame frame;
    private JPanel panel;
    private JButton next;
    private JLabel welcome, to, mms;

    Welcome() {
        frame = new JFrame();
        panel = new JPanel();
        next = new JButton("Next");
        welcome = new JLabel("Welcome");
        to = new JLabel("To");
        mms = new JLabel("Medical Management System");

        welcome.setFont(new Font("Bebas Neue", Font.BOLD, 80));
        to.setFont(new Font("Bebas Neue", Font.BOLD, 50));
        mms.setFont(new Font("Bebas Neue", Font.BOLD, 65));

        frame.setLayout(new BorderLayout());
        panel.setLayout(null);

        panel.setBorder(BorderFactory.createMatteBorder(7, 7, 7, 7, new Color(0, 128, 0)));
        panel.setBackground(new Color(152, 251, 152));
        next.setForeground(new Color(255, 250, 250));
        next.setBackground(new Color(0, 0, 0));
        next.setFocusable(false);

        next.addActionListener(this);

        welcome.setBounds(450, 150, 700, 80);
        to.setBounds(580, 300, 400, 50);
        mms.setBounds(200, 400, 1000, 85);
        next.setBounds(1150, 600, 65, 25);

        frame.add(panel, BorderLayout.CENTER);
        panel.add(welcome);
        panel.add(to);
        panel.add(next);
        panel.add(mms);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == next) {
            new Login();
            frame.dispose();
        }
    }
}

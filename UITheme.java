package com.project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class UITheme {

    // === Colors ===
    public static final Color PRIMARY       = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color DANGER        = new Color(220, 38, 38);
    public static final Color DANGER_HOVER  = new Color(185, 28, 28);
    public static final Color SUCCESS       = new Color(5, 150, 105);
    public static final Color SUCCESS_HOVER = new Color(4, 120, 87);
    public static final Color WARNING_COLOR = new Color(217, 119, 6);
    public static final Color WARNING_HOVER = new Color(180, 83, 9);

    public static final Color SIDEBAR_BG    = new Color(15, 23, 42);
    public static final Color SIDEBAR_BRAND = new Color(9, 14, 28);
    public static final Color SIDEBAR_ITEM  = new Color(30, 41, 59);
    public static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    public static final Color SIDEBAR_TEXT  = new Color(148, 163, 184);

    public static final Color CONTENT_BG    = new Color(248, 250, 252);
    public static final Color CARD_BG       = Color.WHITE;
    public static final Color TEXT_PRIMARY  = new Color(15, 23, 42);
    public static final Color TEXT_MUTED    = new Color(100, 116, 139);
    public static final Color BORDER        = new Color(226, 232, 240);
    public static final Color TABLE_EVEN    = Color.WHITE;
    public static final Color TABLE_ODD     = new Color(248, 250, 252);
    public static final Color TABLE_SEL     = new Color(219, 234, 254);
    public static final Color TABLE_HDR     = new Color(241, 245, 249);

    // === Fonts ===
    public static final Font FONT_BRAND  = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_NAV    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT   = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_STAT_S = new Font("Segoe UI", Font.PLAIN, 12);

    // ===  Buttons ===
    public static JButton createButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg);    btn.repaint(); }
        });
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, PRIMARY_HOVER);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, DANGER_HOVER);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, SUCCESS_HOVER);
    }

    public static JButton createWarningButton(String text) {
        return createButton(text, WARNING_COLOR, WARNING_HOVER);
    }

    public static JButton createNavButton(String icon, String text) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(FONT_NAV);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(SIDEBAR_ITEM);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(14, 28, 14, 28));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
                btn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR_ITEM);
                btn.setForeground(SIDEBAR_TEXT);
            }
        });
        return btn;
    }

    // === Form Components ===
    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(7, 10, 7, 10)
        ));
        tf.setBackground(Color.WHITE);
        return tf;
    }

    public static JComboBox<String> createComboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY);
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return cb;
    }

    public static JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    // === Table ===
    public static void applyTableStyle(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SEL);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(TABLE_EVEN);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setBackground(TABLE_HDR);
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? TABLE_EVEN : TABLE_ODD);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getViewport().setBackground(TABLE_EVEN);
        return sp;
    }

    // === Search Field with placeholder ===
    public static JTextField createSearchField() {
        JTextField tf = new JTextField(16) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(148, 163, 184));
                    g2.setFont(FONT_BODY);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("Search...", 10, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setBackground(CONTENT_BG);
        return tf;
    }

    // === Toolbar (section header with right-side components) ===
    public static JPanel createToolbar(String title, Component... rightComponents) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(10, 20, 10, 16)
        ));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_H2);
        lbl.setForeground(TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(CARD_BG);
        for (Component c : rightComponents) right.add(c);

        panel.add(lbl, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // === Panels ===
    public static JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(14, 20, 14, 20)
        ));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_H2);
        lbl.setForeground(TEXT_PRIMARY);
        panel.add(lbl, BorderLayout.WEST);
        return panel;
    }

    public static JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(18, 20, 18, 20)
            )
        ));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(FONT_STAT_S);
        titleLbl.setForeground(TEXT_MUTED);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(FONT_STAT);
        valueLbl.setForeground(TEXT_PRIMARY);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLbl);
        return card;
    }

    // === Dialog builder ===
    public static JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(24, 30, 16, 30));
        return form;
    }

    public static GridBagConstraints labelConstraints(int row) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = row;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(6, 0, 6, 16);
        return gc;
    }

    public static GridBagConstraints fieldConstraints(int row) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = row;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets = new Insets(6, 0, 6, 0);
        return gc;
    }

    public static JPanel createDialogButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        for (JButton btn : buttons) {
            panel.add(btn);
        }
        return panel;
    }

    public static void styleDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(CONTENT_BG);
    }
}

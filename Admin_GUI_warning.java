package com.project;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class Admin_GUI_warning {
    private JPanel warningPanel;
    private JTable warningList;
    private JScrollPane sp;

    Admin_GUI_warning() {
        warningPanel = new JPanel(new BorderLayout(0, 0));

        Admin_GUI.center.removeAll();
        Admin_GUI.center.add(warningPanel, BorderLayout.CENTER);
        Admin_GUI.center.revalidate();
        Admin_GUI.center.repaint();

        // Toolbar with search
        JTextField searchField = UITheme.createSearchField();

        // Section header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.CARD_BG);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 20, 0, 20)
        ));

        JLabel title = new JLabel("Stock & Expiry Warnings");
        title.setFont(UITheme.FONT_H2);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel badge = new JLabel("  LOW STOCK / EXPIRING SOON  ");
        badge.setFont(UITheme.FONT_SMALL);
        badge.setForeground(Color.WHITE);
        badge.setBackground(UITheme.DANGER);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleRow.setBackground(UITheme.CARD_BG);
        titleRow.add(title);
        titleRow.add(badge);

        header.add(titleRow, BorderLayout.CENTER);

        // Warning notice
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(254, 242, 242));
        notice.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.DANGER),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel noticeText = new JLabel("Drugs listed below have quantity < 15 units OR expiry days < 11. Please review and restock.");
        noticeText.setFont(UITheme.FONT_BODY);
        noticeText.setForeground(new Color(153, 27, 27));
        notice.add(noticeText, BorderLayout.CENTER);

        // Table
        warningList = loadTable("SELECT * FROM `mm_warning`");
        applyWarningTableStyle(warningList);
        sp = UITheme.createScrollPane(warningList);

        // Wire search
        TableRowSorter<TableModel> realSorter = new TableRowSorter<>(warningList.getModel());
        warningList.setRowSorter(realSorter);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String t = searchField.getText().trim();
                realSorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
            }
        });

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        tableCard.add(notice, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        // Content panel
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(UITheme.CONTENT_BG);
        content.add(tableCard, BorderLayout.CENTER);

        JPanel searchBar = UITheme.createToolbar("", searchField);
        searchBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(UITheme.CARD_BG);
        topWrapper.add(header, BorderLayout.NORTH);
        topWrapper.add(searchBar, BorderLayout.SOUTH);

        warningPanel.add(topWrapper, BorderLayout.NORTH);
        warningPanel.add(content, BorderLayout.CENTER);
        warningPanel.setBackground(UITheme.CONTENT_BG);
    }

    private void applyWarningTableStyle(JTable table) {
        UITheme.applyTableStyle(table);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? new Color(255, 245, 245) : new Color(254, 242, 242));
                    setForeground(new Color(153, 27, 27));
                }
                setFont(UITheme.FONT_BODY);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });

        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(new Color(254, 226, 226));
        hdr.setForeground(UITheme.DANGER);
    }

    private JTable loadTable(String sql) {
        List<String> columnNames = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for (int i = 1; i <= cols; i++) columnNames.add(md.getColumnName(i));
            while (rs.next()) {
                List<Object> row = new ArrayList<>(cols);
                for (int i = 1; i <= cols; i++) row.add(rs.getObject(i));
                data.add(row);
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        Vector<String> colVec = new Vector<>(columnNames);
        Vector<Vector<Object>> dataVec = new Vector<>();
        for (List<Object> row : data) dataVec.add(new Vector<>(row));
        return new JTable(dataVec, colVec) {
            @Override public Class<?> getColumnClass(int col) {
                for (int row = 0; row < getRowCount(); row++) {
                    Object o = getValueAt(row, col);
                    if (o != null) return o.getClass();
                }
                return Object.class;
            }
        };
    }
}

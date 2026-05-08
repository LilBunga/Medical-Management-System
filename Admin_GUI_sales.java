package com.project;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class Admin_GUI_sales implements ActionListener {
    private JPanel salesPanel, westPanel, centerPanel;
    private JButton delete, update, updateBtn;
    private JTextField updateNameTf, updatePriceTf, updateQuantityTf;
    private JTable salesList;
    private JScrollPane sp;
    private JComboBox<String> updateTypeCb;
    private TableRowSorter<TableModel> sorter;
    private JTextField fromDateTf, toDateTf;

    Admin_GUI_sales() {
        salesPanel = new JPanel(new BorderLayout(0, 0));

        Admin_GUI.center.removeAll();
        Admin_GUI.center.add(salesPanel, BorderLayout.CENTER);
        Admin_GUI.center.revalidate();
        Admin_GUI.center.repaint();

        // === WEST ===
        westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBackground(UITheme.CARD_BG);
        westPanel.setPreferredSize(new Dimension(160, 0));
        westPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER),
            new EmptyBorder(20, 10, 20, 10)
        ));

        JLabel actionLabel = new JLabel("ACTIONS");
        actionLabel.setFont(UITheme.FONT_SMALL);
        actionLabel.setForeground(UITheme.TEXT_MUTED);
        actionLabel.setBorder(new EmptyBorder(0, 6, 10, 0));
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        update = UITheme.createWarningButton("Update");
        delete = UITheme.createDangerButton("Delete");

        for (JButton btn : new JButton[]{update, delete}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        westPanel.add(actionLabel);
        westPanel.add(update);
        westPanel.add(Box.createVerticalStrut(8));
        westPanel.add(delete);
        westPanel.add(Box.createVerticalGlue());

        // === CENTER ===
        centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.setBackground(UITheme.CONTENT_BG);

        // Toolbar with search + export
        JTextField searchField = UITheme.createSearchField();
        JButton exportBtn = UITheme.createButton("Export CSV", new Color(71, 85, 105), new Color(51, 65, 85));
        exportBtn.setBorder(new EmptyBorder(6, 12, 6, 12));

        JPanel toolbar = UITheme.createToolbar("Sales Transactions", searchField, exportBtn);

        // Date filter bar
        fromDateTf = UITheme.createTextField();
        fromDateTf.setPreferredSize(new Dimension(110, 32));
        fromDateTf.setToolTipText("dd/MM/yyyy");

        toDateTf = UITheme.createTextField();
        toDateTf.setPreferredSize(new Dimension(110, 32));
        toDateTf.setToolTipText("dd/MM/yyyy");

        JButton applyFilter = UITheme.createPrimaryButton("Filter");
        applyFilter.setBorder(new EmptyBorder(5, 12, 5, 12));
        JButton clearFilter = UITheme.createButton("Clear", new Color(100, 116, 139), new Color(71, 85, 105));
        clearFilter.setBorder(new EmptyBorder(5, 12, 5, 12));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterBar.setBackground(UITheme.CONTENT_BG);
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JLabel fromLbl = new JLabel("From:");
        fromLbl.setFont(UITheme.FONT_LABEL);
        fromLbl.setForeground(UITheme.TEXT_MUTED);
        JLabel toLbl = new JLabel("To:");
        toLbl.setFont(UITheme.FONT_LABEL);
        toLbl.setForeground(UITheme.TEXT_MUTED);
        JLabel hint = new JLabel("  format: dd/MM/yyyy");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);

        filterBar.add(fromLbl);
        filterBar.add(fromDateTf);
        filterBar.add(toLbl);
        filterBar.add(toDateTf);
        filterBar.add(applyFilter);
        filterBar.add(clearFilter);
        filterBar.add(hint);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(UITheme.CONTENT_BG);
        northWrapper.add(toolbar, BorderLayout.NORTH);
        northWrapper.add(filterBar, BorderLayout.SOUTH);
        centerPanel.add(northWrapper, BorderLayout.NORTH);

        // Table
        salesList = loadTable("SELECT * FROM `mm_sales`");
        UITheme.applyTableStyle(salesList);
        sp = UITheme.createScrollPane(salesList);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(new EmptyBorder(16, 16, 0, 16));
        tableCard.add(sp, BorderLayout.CENTER);

        // Stats bar
        String today = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        int total = 0, grandTotal = 0;
        for (int x = 0; x < salesList.getRowCount(); x++) {
            int amount = Integer.parseInt(salesList.getValueAt(x, 5).toString());
            grandTotal += amount;
            if (today.equals(salesList.getValueAt(x, 6).toString())) total += amount;
        }

        JPanel statsBar = new JPanel(new GridLayout(1, 2, 16, 0));
        statsBar.setBackground(UITheme.CARD_BG);
        statsBar.setBorder(new EmptyBorder(16, 16, 16, 16));
        statsBar.add(buildStatCard("Today's Revenue", "Rp " + String.format("%,d", total), UITheme.SUCCESS));
        statsBar.add(buildStatCard("Grand Total Revenue", "Rp " + String.format("%,d", grandTotal), UITheme.PRIMARY));

        centerPanel.add(tableCard, BorderLayout.CENTER);
        centerPanel.add(statsBar, BorderLayout.SOUTH);

        // Sorter & wiring
        sorter = new TableRowSorter<>(salesList.getModel());
        salesList.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
        });

        applyFilter.addActionListener(e -> applyDateFilter());
        clearFilter.addActionListener(e -> {
            fromDateTf.setText("");
            toDateTf.setText("");
            sorter.setRowFilter(null);
        });

        exportBtn.addActionListener(e -> ExportUtils.exportToCSV(salesList, "Sales", Admin_GUI.frame));

        delete.addActionListener(this);
        update.addActionListener(this);

        salesPanel.add(westPanel, BorderLayout.WEST);
        salesPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void applySearch(String text) {
        String trimmed = text.trim();
        sorter.setRowFilter(trimmed.isEmpty() ? null : RowFilter.regexFilter("(?i)" + trimmed));
    }

    private void applyDateFilter() {
        String from = fromDateTf.getText().trim();
        String to   = toDateTf.getText().trim();

        if (from.isEmpty() && to.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        // Validate format
        try {
            if (!from.isEmpty()) sdf.parse(from);
            if (!to.isEmpty())   sdf.parse(to);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(Admin_GUI.frame,
                "Invalid date format. Please use dd/MM/yyyy", "Filter Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                try {
                    String dateStr = entry.getValue(6).toString();
                    Date rowDate   = sdf.parse(dateStr);
                    if (!from.isEmpty() && rowDate.before(sdf.parse(from))) return false;
                    if (!to.isEmpty()   && rowDate.after(sdf.parse(to)))   return false;
                    return true;
                } catch (ParseException e) {
                    return true;
                }
            }
        });
    }

    private JPanel buildStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.CONTENT_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(12, 16, 12, 16)
            )
        ));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLbl.setForeground(UITheme.TEXT_PRIMARY);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLbl);
        return card;
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

    private void renumberIds(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.addBatch("SET @num := 0;");
            st.addBatch("UPDATE `mm_sales` SET SN = @num := (@num+1);");
            st.addBatch("ALTER TABLE `mm_sales` AUTO_INCREMENT = 1;");
            st.executeBatch();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == delete)         handleDelete();
        else if (ae.getSource() == update)    showUpdateDialog();
        else if (ae.getSource() == updateBtn) handleUpdate();
    }

    private void handleDelete() {
        int rowNo = salesList.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Sales — Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(Admin_GUI.frame, "Delete this sales record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            int modelRow = salesList.convertRowIndexToModel(rowNo);
            int sn = (int) salesList.getModel().getValueAt(modelRow, 0);
            PreparedStatement ps = con.prepareStatement("DELETE FROM `mm_sales` WHERE SN = ?");
            ps.setInt(1, sn); ps.executeUpdate(); ps.close();
            renumberIds(con);
        } catch (Exception e) { System.out.println(e); }
        new Admin_GUI_sales();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Record deleted successfully.", "Sales — Delete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateDialog() {
        int rowNo = salesList.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Sales — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = salesList.convertRowIndexToModel(rowNo);
        JDialog dialog = new JDialog(Admin_GUI.frame, "Update Sale", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(Admin_GUI.frame);
        dialog.setLayout(new BorderLayout());

        JPanel form = UITheme.createFormPanel();
        updateNameTf     = UITheme.createTextField();
        updateTypeCb     = UITheme.createComboBox("Select drug type", "Medicine", "Syrup");
        updatePriceTf    = UITheme.createTextField();
        updateQuantityTf = UITheme.createTextField();

        updateNameTf.setText(salesList.getModel().getValueAt(modelRow, 1).toString());
        updatePriceTf.setText(salesList.getModel().getValueAt(modelRow, 3).toString());
        updateQuantityTf.setText(salesList.getModel().getValueAt(modelRow, 4).toString());

        form.add(UITheme.createFormLabel("Name"),     UITheme.labelConstraints(0));
        form.add(updateNameTf,                         UITheme.fieldConstraints(0));
        form.add(UITheme.createFormLabel("Type"),     UITheme.labelConstraints(1));
        form.add(updateTypeCb,                         UITheme.fieldConstraints(1));
        form.add(UITheme.createFormLabel("Price"),    UITheme.labelConstraints(2));
        form.add(updatePriceTf,                        UITheme.fieldConstraints(2));
        form.add(UITheme.createFormLabel("Quantity"), UITheme.labelConstraints(3));
        form.add(updateQuantityTf,                     UITheme.fieldConstraints(3));

        updateBtn = UITheme.createWarningButton("Save Changes");
        updateBtn.addActionListener(this);
        JButton cancel = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(UITheme.createSectionHeader("Update Sale Record"), BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(UITheme.createDialogButtonPanel(cancel, updateBtn), BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void handleUpdate() {
        if (updateNameTf.getText().isEmpty() || updateTypeCb.getSelectedItem().equals("Select drug type")
                || updatePriceTf.getText().isEmpty() || updateQuantityTf.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please fill in all fields.", "Sales — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (salesList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Sales — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int price, quantity;
        try {
            price    = Integer.parseInt(updatePriceTf.getText());
            quantity = Integer.parseInt(updateQuantityTf.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Price and Quantity must be whole numbers.", "Sales — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            int modelRow = salesList.convertRowIndexToModel(salesList.getSelectedRow());
            int sn = (int) salesList.getModel().getValueAt(modelRow, 0);
            PreparedStatement ps = con.prepareStatement(
                "UPDATE `mm_sales` SET `Name`=?, `Type`=?, `Price`=?, `Quantity`=? WHERE SN = ?");
            ps.setString(1, updateNameTf.getText());
            ps.setString(2, (String) updateTypeCb.getSelectedItem());
            ps.setInt(3, price);
            ps.setInt(4, quantity);
            ps.setInt(5, sn);
            ps.executeUpdate(); ps.close();
        } catch (Exception e) { System.out.println(e); return; }
        new Admin_GUI_sales();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Record updated successfully.", "Sales — Update", JOptionPane.INFORMATION_MESSAGE);
    }
}

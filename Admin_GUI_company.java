package com.project;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class Admin_GUI_company implements ActionListener {
    private JPanel companyPanel, westPanel, centerPanel;
    private JButton delete, update, add, addBtn, updateBtn;
    private JTextField addNameTf, addAddressTf, addPhoneNoTf;
    private JTextField updateNameTf, updateAddressTf, updatePhoneNoTf;
    private JTable companyList;
    private JScrollPane sp;
    private TableRowSorter<TableModel> sorter;

    Admin_GUI_company() {
        companyPanel = new JPanel(new BorderLayout(0, 0));

        Admin_GUI.center.removeAll();
        Admin_GUI.center.add(companyPanel, BorderLayout.CENTER);
        Admin_GUI.center.revalidate();
        Admin_GUI.center.repaint();

        // === WEST: Action buttons ===
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

        add    = UITheme.createPrimaryButton("Add Company");
        update = UITheme.createWarningButton("Update");
        delete = UITheme.createDangerButton("Delete");

        for (JButton btn : new JButton[]{add, update, delete}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        westPanel.add(actionLabel);
        westPanel.add(add);
        westPanel.add(Box.createVerticalStrut(8));
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

        JPanel toolbar = UITheme.createToolbar("Company / Supplier List", searchField, exportBtn);
        centerPanel.add(toolbar, BorderLayout.NORTH);

        // Table
        companyList = loadTable("SELECT * FROM `mm_company`");
        UITheme.applyTableStyle(companyList);
        sp = UITheme.createScrollPane(companyList);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        tableCard.add(sp, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        // Sorter & Search wiring
        sorter = new TableRowSorter<>(companyList.getModel());
        companyList.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
        });

        exportBtn.addActionListener(e -> ExportUtils.exportToCSV(companyList, "Companies", Admin_GUI.frame));

        add.addActionListener(this);
        delete.addActionListener(this);
        update.addActionListener(this);

        companyPanel.add(westPanel, BorderLayout.WEST);
        companyPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void applySearch(String text) {
        String trimmed = text.trim();
        sorter.setRowFilter(trimmed.isEmpty() ? null : RowFilter.regexFilter("(?i)" + trimmed));
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == add)            showAddDialog();
        else if (ae.getSource() == addBtn)    handleAdd();
        else if (ae.getSource() == delete)    handleDelete();
        else if (ae.getSource() == update)    showUpdateDialog();
        else if (ae.getSource() == updateBtn) handleUpdate();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(Admin_GUI.frame, "Add Company", true);
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(Admin_GUI.frame);
        dialog.setLayout(new BorderLayout());

        JPanel form = UITheme.createFormPanel();
        addNameTf    = UITheme.createTextField();
        addAddressTf = UITheme.createTextField();
        addPhoneNoTf = UITheme.createTextField();

        form.add(UITheme.createFormLabel("Name"),      UITheme.labelConstraints(0));
        form.add(addNameTf,                             UITheme.fieldConstraints(0));
        form.add(UITheme.createFormLabel("Address"),   UITheme.labelConstraints(1));
        form.add(addAddressTf,                          UITheme.fieldConstraints(1));
        form.add(UITheme.createFormLabel("Phone No."), UITheme.labelConstraints(2));
        form.add(addPhoneNoTf,                          UITheme.fieldConstraints(2));

        addBtn = UITheme.createPrimaryButton("Add Company");
        addBtn.addActionListener(this);
        JButton cancel = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(UITheme.createSectionHeader("Add New Company"), BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(UITheme.createDialogButtonPanel(cancel, addBtn), BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void handleAdd() {
        if (addNameTf.getText().isEmpty() || addAddressTf.getText().isEmpty() || addPhoneNoTf.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please fill in all fields.", "Company — Add", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO `mm_company`(`Name`, `Address`, `Phone No.`) VALUES (?, ?, ?)");
            ps.setString(1, addNameTf.getText());
            ps.setString(2, addAddressTf.getText());
            ps.setString(3, addPhoneNoTf.getText());
            ps.executeUpdate(); ps.close();
        } catch (Exception e) { System.out.println(e); }
        new Admin_GUI_company();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Company added successfully.", "Company — Add", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete() {
        if (companyList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Company — Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(Admin_GUI.frame, "Delete this company?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            int modelRow = companyList.convertRowIndexToModel(companyList.getSelectedRow());
            String name = companyList.getModel().getValueAt(modelRow, 0).toString();
            PreparedStatement ps = con.prepareStatement("DELETE FROM `mm_company` WHERE Name = ?");
            ps.setString(1, name); ps.executeUpdate(); ps.close();
        } catch (Exception e) { System.out.println(e); }
        new Admin_GUI_company();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Company deleted successfully.", "Company — Delete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateDialog() {
        int rowNo = companyList.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Company — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = companyList.convertRowIndexToModel(rowNo);
        JDialog dialog = new JDialog(Admin_GUI.frame, "Update Company", true);
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(Admin_GUI.frame);
        dialog.setLayout(new BorderLayout());

        JPanel form = UITheme.createFormPanel();
        updateNameTf    = UITheme.createTextField();
        updateAddressTf = UITheme.createTextField();
        updatePhoneNoTf = UITheme.createTextField();

        updateNameTf.setText(companyList.getModel().getValueAt(modelRow, 0).toString());
        updateAddressTf.setText(companyList.getModel().getValueAt(modelRow, 1).toString());
        updatePhoneNoTf.setText(companyList.getModel().getValueAt(modelRow, 2).toString());

        form.add(UITheme.createFormLabel("Name"),      UITheme.labelConstraints(0));
        form.add(updateNameTf,                          UITheme.fieldConstraints(0));
        form.add(UITheme.createFormLabel("Address"),   UITheme.labelConstraints(1));
        form.add(updateAddressTf,                       UITheme.fieldConstraints(1));
        form.add(UITheme.createFormLabel("Phone No."), UITheme.labelConstraints(2));
        form.add(updatePhoneNoTf,                       UITheme.fieldConstraints(2));

        updateBtn = UITheme.createWarningButton("Save Changes");
        updateBtn.addActionListener(this);
        JButton cancel = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(UITheme.createSectionHeader("Update Company"), BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(UITheme.createDialogButtonPanel(cancel, updateBtn), BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void handleUpdate() {
        if (updateNameTf.getText().isEmpty() || updateAddressTf.getText().isEmpty() || updatePhoneNoTf.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please fill in all fields.", "Company — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (companyList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Company — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            int modelRow = companyList.convertRowIndexToModel(companyList.getSelectedRow());
            String originalName = companyList.getModel().getValueAt(modelRow, 0).toString();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE `mm_company` SET `Name`=?, `Address`=?, `Phone No.`=? WHERE Name = ?");
            ps.setString(1, updateNameTf.getText());
            ps.setString(2, updateAddressTf.getText());
            ps.setString(3, updatePhoneNoTf.getText());
            ps.setString(4, originalName);
            ps.executeUpdate(); ps.close();
        } catch (Exception e) { System.out.println(e); }
        new Admin_GUI_company();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Company updated successfully.", "Company — Update", JOptionPane.INFORMATION_MESSAGE);
    }
}

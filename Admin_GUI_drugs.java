package com.project;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class Admin_GUI_drugs implements ActionListener {
    private JPanel drugsPanel, westPanel, centerPanel;
    private JButton delete, update, add, addBtn, updateBtn, salesOpen, salesBtn;
    private JTextField addNameTf, addPriceTf, addExpiryDateTf, addCompanyTf, addShelfNumberTf, addQuantityTf;
    private JTextField salesQuantityTf, updateNameTf, updatePriceTf, updateExpiryDateTf, updateCompanyTf, updateShelfNumberTf, updateQuantityTf;
    private JTable drugsList;
    private JScrollPane sp;
    private JComboBox<String> addTypeCb, updateTypeCb;
    private TableRowSorter<TableModel> sorter;

    Admin_GUI_drugs() {
        drugsPanel = new JPanel(new BorderLayout(0, 0));

        Admin_GUI.center.removeAll();
        Admin_GUI.center.add(drugsPanel, BorderLayout.CENTER);
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

        add       = UITheme.createPrimaryButton("Add Drug");
        update    = UITheme.createWarningButton("Update");
        delete    = UITheme.createDangerButton("Delete");
        salesOpen = UITheme.createSuccessButton("Sales");

        for (JButton btn : new JButton[]{add, update, delete, salesOpen}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        westPanel.add(actionLabel);
        westPanel.add(add);
        westPanel.add(Box.createVerticalStrut(8));
        westPanel.add(update);
        westPanel.add(Box.createVerticalStrut(8));
        westPanel.add(delete);
        westPanel.add(Box.createVerticalStrut(8));
        westPanel.add(salesOpen);
        westPanel.add(Box.createVerticalGlue());

        // === CENTER ===
        centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.setBackground(UITheme.CONTENT_BG);

        // Toolbar with search + export
        JTextField searchField = UITheme.createSearchField();
        JButton exportBtn = UITheme.createButton("Export CSV", new Color(71, 85, 105), new Color(51, 65, 85));
        exportBtn.setBorder(new EmptyBorder(6, 12, 6, 12));

        JPanel toolbar = UITheme.createToolbar("Drug Inventory", searchField, exportBtn);
        centerPanel.add(toolbar, BorderLayout.NORTH);

        // Table
        drugsList = loadTable("SELECT * FROM `mm_drugs`");
        UITheme.applyTableStyle(drugsList);
        sp = UITheme.createScrollPane(drugsList);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        tableCard.add(sp, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        // Sorter & Search wiring
        sorter = new TableRowSorter<>(drugsList.getModel());
        drugsList.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { applySearch(searchField.getText()); }
        });

        exportBtn.addActionListener(e -> ExportUtils.exportToCSV(drugsList, "Drugs", Admin_GUI.frame));

        add.addActionListener(this);
        delete.addActionListener(this);
        update.addActionListener(this);
        salesOpen.addActionListener(this);

        drugsPanel.add(westPanel, BorderLayout.WEST);
        drugsPanel.add(centerPanel, BorderLayout.CENTER);
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

    private JComboBox<String> createDrugTypeCombo() {
        return UITheme.createComboBox("Select drug type", "Medicine", "Syrup");
    }

    private void refreshWarnings(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.addBatch("DELETE FROM `mm_warning`;");
            st.addBatch("INSERT INTO `mm_warning` SELECT `Name`, `Type`, `Expiry day's`, `Quantity` FROM `mm_drugs` WHERE `Expiry day's` < 11 OR `Quantity` < 15;");
            st.executeBatch();
        }
    }

    private void renumberIds(Connection con, String table) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.addBatch("SET @num := 0;");
            st.addBatch("UPDATE `" + table + "` SET SN = @num := (@num+1);");
            st.addBatch("ALTER TABLE `" + table + "` AUTO_INCREMENT = 1;");
            st.executeBatch();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == add)            showAddDialog();
        else if (ae.getSource() == addBtn)    handleAdd();
        else if (ae.getSource() == delete)    handleDelete();
        else if (ae.getSource() == update)    showUpdateDialog();
        else if (ae.getSource() == updateBtn) handleUpdate();
        else if (ae.getSource() == salesOpen) showSalesDialog();
        else if (ae.getSource() == salesBtn)  handleSales();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(Admin_GUI.frame, "Add Drug", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(Admin_GUI.frame);
        dialog.setLayout(new BorderLayout());

        JPanel form = UITheme.createFormPanel();
        addNameTf        = UITheme.createTextField();
        addTypeCb        = createDrugTypeCombo();
        addPriceTf       = UITheme.createTextField();
        addExpiryDateTf  = UITheme.createTextField();
        addCompanyTf     = UITheme.createTextField();
        addShelfNumberTf = UITheme.createTextField();
        addQuantityTf    = UITheme.createTextField();

        String[] labels = {"Name", "Type", "Price", "Expiry Days", "Company", "Shelf No.", "Quantity"};
        Component[] fields = {addNameTf, addTypeCb, addPriceTf, addExpiryDateTf, addCompanyTf, addShelfNumberTf, addQuantityTf};
        for (int i = 0; i < labels.length; i++) {
            form.add(UITheme.createFormLabel(labels[i]), UITheme.labelConstraints(i));
            form.add(fields[i], UITheme.fieldConstraints(i));
        }

        addBtn = UITheme.createPrimaryButton("Add Drug");
        addBtn.addActionListener(this);
        JButton cancel = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(UITheme.createSectionHeader("Add New Drug"), BorderLayout.NORTH);
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(UITheme.createDialogButtonPanel(cancel, addBtn), BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void handleAdd() {
        if (addNameTf.getText().isEmpty() || addTypeCb.getSelectedItem().equals("Select drug type")
                || addPriceTf.getText().isEmpty() || addExpiryDateTf.getText().isEmpty()
                || addCompanyTf.getText().isEmpty() || addShelfNumberTf.getText().isEmpty()
                || addQuantityTf.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please fill in all fields.", "Drugs — Add", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int price, expiryDays, shelfNo, quantity;
        try {
            price     = Integer.parseInt(addPriceTf.getText());
            expiryDays = Integer.parseInt(addExpiryDateTf.getText());
            shelfNo   = Integer.parseInt(addShelfNumberTf.getText());
            quantity  = Integer.parseInt(addQuantityTf.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Price, Expiry Days, Shelf No., and Quantity must be whole numbers.", "Drugs — Add", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO `mm_drugs` (`Name`, `Type`, `Price`, `Expiry day's`, `Company`, `Shelf No.`, `Quantity`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, addNameTf.getText());
            ps.setString(2, (String) addTypeCb.getSelectedItem());
            ps.setInt(3, price);
            ps.setInt(4, expiryDays);
            ps.setString(5, addCompanyTf.getText());
            ps.setInt(6, shelfNo);
            ps.setInt(7, quantity);
            ps.executeUpdate(); ps.close();
            if (quantity < 15 || expiryDays < 10) {
                refreshWarnings(con);
            }
        } catch (Exception e) { System.out.println(e); return; }
        new Admin_GUI_drugs();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Drug added successfully.", "Drugs — Add", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete() {
        if (drugsList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Drugs — Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(Admin_GUI.frame, "Are you sure you want to delete this drug?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            int sn       = (int) drugsList.getModel().getValueAt(drugsList.convertRowIndexToModel(drugsList.getSelectedRow()), 0);
            String name  = drugsList.getModel().getValueAt(drugsList.convertRowIndexToModel(drugsList.getSelectedRow()), 1).toString();
            String type  = drugsList.getModel().getValueAt(drugsList.convertRowIndexToModel(drugsList.getSelectedRow()), 2).toString();
            int quantity = (int) drugsList.getModel().getValueAt(drugsList.convertRowIndexToModel(drugsList.getSelectedRow()), 7);
            int ed       = (int) drugsList.getModel().getValueAt(drugsList.convertRowIndexToModel(drugsList.getSelectedRow()), 4);

            PreparedStatement ps1 = con.prepareStatement("DELETE FROM `mm_drugs` WHERE SN = ?");
            ps1.setInt(1, sn); ps1.executeUpdate(); ps1.close();

            PreparedStatement ps2 = con.prepareStatement(
                "DELETE FROM `mm_warning` WHERE Name = ? AND Type = ? AND Quantity = ? AND `Expiry day's` = ?");
            ps2.setString(1, name); ps2.setString(2, type);
            ps2.setInt(3, quantity); ps2.setInt(4, ed);
            ps2.executeUpdate(); ps2.close();

            renumberIds(con, "mm_drugs");
        } catch (Exception e) { System.out.println(e); }
        new Admin_GUI_drugs();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Drug deleted successfully.", "Drugs — Delete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateDialog() {
        if (drugsList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Drugs — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = drugsList.convertRowIndexToModel(drugsList.getSelectedRow());
        JDialog dialog = new JDialog(Admin_GUI.frame, "Update Drug", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(Admin_GUI.frame);
        dialog.setLayout(new BorderLayout());

        JPanel form = UITheme.createFormPanel();
        updateNameTf        = UITheme.createTextField();
        updateTypeCb        = createDrugTypeCombo();
        updatePriceTf       = UITheme.createTextField();
        updateExpiryDateTf  = UITheme.createTextField();
        updateCompanyTf     = UITheme.createTextField();
        updateShelfNumberTf = UITheme.createTextField();
        updateQuantityTf    = UITheme.createTextField();

        updateNameTf.setText(drugsList.getModel().getValueAt(modelRow, 1).toString());
        updatePriceTf.setText(drugsList.getModel().getValueAt(modelRow, 3).toString());
        updateExpiryDateTf.setText(drugsList.getModel().getValueAt(modelRow, 4).toString());
        updateCompanyTf.setText(drugsList.getModel().getValueAt(modelRow, 5).toString());
        updateShelfNumberTf.setText(drugsList.getModel().getValueAt(modelRow, 6).toString());
        updateQuantityTf.setText(drugsList.getModel().getValueAt(modelRow, 7).toString());

        String[] labels = {"Name", "Type", "Price", "Expiry Days", "Company", "Shelf No.", "Quantity"};
        Component[] fields = {updateNameTf, updateTypeCb, updatePriceTf, updateExpiryDateTf, updateCompanyTf, updateShelfNumberTf, updateQuantityTf};
        for (int i = 0; i < labels.length; i++) {
            form.add(UITheme.createFormLabel(labels[i]), UITheme.labelConstraints(i));
            form.add(fields[i], UITheme.fieldConstraints(i));
        }

        updateBtn = UITheme.createWarningButton("Save Changes");
        updateBtn.addActionListener(this);
        JButton cancel = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(UITheme.createSectionHeader("Update Drug"), BorderLayout.NORTH);
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(UITheme.createDialogButtonPanel(cancel, updateBtn), BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void handleUpdate() {
        if (updateNameTf.getText().isEmpty() || updateTypeCb.getSelectedItem().equals("Select drug type")
                || updatePriceTf.getText().isEmpty() || updateExpiryDateTf.getText().isEmpty()
                || updateCompanyTf.getText().isEmpty() || updateShelfNumberTf.getText().isEmpty()
                || updateQuantityTf.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please fill in all fields.", "Drugs — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (drugsList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a row first.", "Drugs — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int price, expiryDays, shelfNo, quantity;
        try {
            price     = Integer.parseInt(updatePriceTf.getText());
            expiryDays = Integer.parseInt(updateExpiryDateTf.getText());
            shelfNo   = Integer.parseInt(updateShelfNumberTf.getText());
            quantity  = Integer.parseInt(updateQuantityTf.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Price, Expiry Days, Shelf No., and Quantity must be whole numbers.", "Drugs — Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            int sn = (int) drugsList.getModel().getValueAt(drugsList.convertRowIndexToModel(drugsList.getSelectedRow()), 0);
            PreparedStatement ps = con.prepareStatement(
                "UPDATE `mm_drugs` SET `Name`=?, `Type`=?, `Price`=?, `Expiry day's`=?, `Company`=?, `Shelf No.`=?, `Quantity`=? WHERE SN = ?");
            ps.setString(1, updateNameTf.getText());
            ps.setString(2, (String) updateTypeCb.getSelectedItem());
            ps.setInt(3, price);
            ps.setInt(4, expiryDays);
            ps.setString(5, updateCompanyTf.getText());
            ps.setInt(6, shelfNo);
            ps.setInt(7, quantity);
            ps.setInt(8, sn);
            ps.executeUpdate(); ps.close();
            if (quantity < 15 || expiryDays < 10) {
                refreshWarnings(con);
            }
        } catch (Exception e) { System.out.println(e); return; }
        new Admin_GUI_drugs();
        JOptionPane.showMessageDialog(Admin_GUI.frame, "Drug updated successfully.", "Drugs — Update", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSalesDialog() {
        if (drugsList.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please select a drug first.", "Drugs — Sales", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow  = drugsList.convertRowIndexToModel(drugsList.getSelectedRow());
        String drugName = drugsList.getModel().getValueAt(modelRow, 1).toString();
        int available   = (int) drugsList.getModel().getValueAt(modelRow, 7);

        JDialog dialog = new JDialog(Admin_GUI.frame, "Add to Sales", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(Admin_GUI.frame);
        dialog.setLayout(new BorderLayout());

        JPanel form = UITheme.createFormPanel();
        JTextField drugNameDisplay = UITheme.createTextField();
        drugNameDisplay.setText(drugName);
        drugNameDisplay.setEditable(false);
        drugNameDisplay.setBackground(new Color(248, 250, 252));
        salesQuantityTf = UITheme.createTextField();

        JLabel availLbl = UITheme.createFormLabel("Available: " + available + " units");
        availLbl.setForeground(UITheme.SUCCESS);

        form.add(UITheme.createFormLabel("Drug"),     UITheme.labelConstraints(0));
        form.add(drugNameDisplay,                      UITheme.fieldConstraints(0));
        form.add(UITheme.createFormLabel("Quantity"), UITheme.labelConstraints(1));
        form.add(salesQuantityTf,                      UITheme.fieldConstraints(1));
        form.add(availLbl,                             UITheme.fieldConstraints(2));

        salesBtn = UITheme.createSuccessButton("Confirm Sale");
        salesBtn.addActionListener(this);
        JButton cancel = UITheme.createButton("Cancel", new Color(100, 116, 139), new Color(71, 85, 105));
        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(UITheme.createSectionHeader("Record Sale"), BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(UITheme.createDialogButtonPanel(cancel, salesBtn), BorderLayout.SOUTH);
        UITheme.styleDialog(dialog);
        dialog.setVisible(true);
    }

    private void handleSales() {
        if (salesQuantityTf.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Please enter a quantity.", "Drugs — Sales", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = drugsList.convertRowIndexToModel(drugsList.getSelectedRow());
        int qv;
        try {
            qv = Integer.parseInt(salesQuantityTf.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Quantity must be a whole number.", "Drugs — Sales", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            int sn  = (int) drugsList.getModel().getValueAt(modelRow, 0);
            int av  = (int) drugsList.getModel().getValueAt(modelRow, 7);
            int pr  = (int) drugsList.getModel().getValueAt(modelRow, 3);

            if (qv > av) {
                JOptionPane.showMessageDialog(Admin_GUI.frame, "Quantity exceeds available stock (" + av + ").", "Drugs — Sales", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int total = pr * qv;
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO `mm_sales`(`Name`, `Type`, `Price`, `Quantity`, `Total Price`, `Date`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, drugsList.getModel().getValueAt(modelRow, 1).toString());
            ps.setString(2, drugsList.getModel().getValueAt(modelRow, 2).toString());
            ps.setInt(3, pr); ps.setInt(4, qv); ps.setInt(5, total); ps.setString(6, date);
            ps.executeUpdate(); ps.close();

            int newQty = av - qv;
            PreparedStatement ps2 = con.prepareStatement("UPDATE `mm_drugs` SET `Quantity`=? WHERE SN = ?");
            ps2.setInt(1, newQty); ps2.setInt(2, sn); ps2.executeUpdate(); ps2.close();

            if (newQty <= 0) {
                PreparedStatement ps3 = con.prepareStatement("DELETE FROM `mm_drugs` WHERE SN = ?");
                ps3.setInt(1, sn); ps3.executeUpdate(); ps3.close();
            }
            renumberIds(con, "mm_drugs");
            JOptionPane.showMessageDialog(Admin_GUI.frame, "Sale recorded successfully.", "Drugs — Sales", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { System.out.println(e); }
        salesQuantityTf.setText("");
        new Admin_GUI_drugs();
    }
}

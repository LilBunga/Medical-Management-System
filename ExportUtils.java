package com.project;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportUtils {

    public static void exportToCSV(JTable table, String moduleName, JFrame parent) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "No data to export.", "Export CSV", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV File");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        chooser.setSelectedFile(new File(moduleName + "_" + timestamp + ".csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            // BOM for Excel UTF-8 compatibility
            pw.print('\uFEFF');

            // Header row
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (i > 0) sb.append(",");
                sb.append(escapeCSV(table.getColumnName(i)));
            }
            pw.println(sb);

            // Data rows (respects current filter/sort)
            for (int row = 0; row < table.getRowCount(); row++) {
                sb = new StringBuilder();
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col > 0) sb.append(",");
                    Object val = table.getValueAt(row, col);
                    sb.append(escapeCSV(val == null ? "" : val.toString()));
                }
                pw.println(sb);
            }

            JOptionPane.showMessageDialog(parent,
                "Successfully exported " + table.getRowCount() + " rows.\n\nFile: " + file.getAbsolutePath(),
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                "Export failed: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

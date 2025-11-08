package courier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistoryForm extends JFrame {

    private JTextField userIdField;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public HistoryForm() {
        setTitle("Courier History");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter User ID:"));
        userIdField = new JTextField(10);
        topPanel.add(userIdField);

        JButton showBtn = new JButton("Show History");
        showBtn.addActionListener(this::loadHistory);
        topPanel.add(showBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {"TRACKING ID", "RECEIVER", "SOURCE", "DESTINATION", "WEIGHT", "STATUS"};
        tableModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(tableModel);

        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private void loadHistory(ActionEvent e) {
        String userIdText = userIdField.getText().trim();
        if (userIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a valid User ID.");
            return;
        }

        tableModel.setRowCount(0); // clear old data

        try (Connection con = DBHelper.getConnection()) {

            String sql = "SELECT tracking_id, receiver, source, destination, weight, status FROM couriers WHERE user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(userIdText));

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                Object[] row = {
                        rs.getString("tracking_id"),
                        rs.getString("receiver"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDouble("weight"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No courier history found for this User.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HistoryForm().setVisible(true));
    }
}

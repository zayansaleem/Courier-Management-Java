package courier;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class TrackingForm extends JFrame {

    private JPanel userPanel;
    private JTextField userIdField;
    private JButton verifyBtn;
    private JLabel verifiedUserLabel;
    private int currentUserId = -1;

    private JPanel trackingPanel;
    private JTextField tidField;
    private JTextPane resultArea;   // ✅ changed to JTextPane
    private JButton searchBtn;

    public TrackingForm() {
        setTitle("Track Courier");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---- USER PANEL ----
        userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        userPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField(10);
        userPanel.add(userIdField);

        verifyBtn = new JButton("Verify ID");
        verifyBtn.addActionListener(this::verifyUser);
        userPanel.add(verifyBtn);

        verifiedUserLabel = new JLabel(" | Status: Awaiting ID");
        userPanel.add(verifiedUserLabel);

        add(userPanel, BorderLayout.NORTH);

        // ---- TRACK PANEL ----
        trackingPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputPanel.add(new JLabel("Tracking ID:"));
        tidField = new JTextField(15);
        inputPanel.add(tidField);

        searchBtn = new JButton("Track");
        searchBtn.addActionListener(this::searchCourier);
        inputPanel.add(searchBtn);

        trackingPanel.add(inputPanel, BorderLayout.NORTH);

        // ✅ JTextPane instead of JTextArea
        resultArea = new JTextPane();
        resultArea.setEditable(false);

        trackingPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(trackingPanel, BorderLayout.CENTER);

        trackingPanel.setVisible(false);
        setLocationRelativeTo(null);
    }

    // ✅ Method to insert bold + normal text
    private void appendBold(String bold, String normal) {
        try {
            StyledDocument doc = resultArea.getStyledDocument();

            SimpleAttributeSet boldStyle = new SimpleAttributeSet();
            StyleConstants.setBold(boldStyle, true);

            SimpleAttributeSet normalStyle = new SimpleAttributeSet();
            StyleConstants.setBold(normalStyle, false);

            doc.insertString(doc.getLength(), bold, boldStyle);
            doc.insertString(doc.getLength(), normal + "\n", normalStyle);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void verifyUser(ActionEvent e) {
        String userIdText = userIdField.getText().trim();
        if (userIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID.");
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT id, name FROM users WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(userIdText));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                String name = rs.getString("name");

                verifiedUserLabel.setText(" | Status: Verified for " + name);
                trackingPanel.setVisible(true);
                userIdField.setEditable(false);
                verifyBtn.setEnabled(false);

            } else {
                verifiedUserLabel.setText(" | Status: ID Not Found!");
                trackingPanel.setVisible(false);
                currentUserId = -1;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void searchCourier(ActionEvent e) {
        if (currentUserId == -1) {
            resultArea.setText("Please verify your User ID first.");
            return;
        }

        String trackingId = tidField.getText().trim();
        if (trackingId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Tracking ID.");
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT * FROM couriers WHERE tracking_id = ? AND user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, trackingId);
            ps.setInt(2, currentUserId);

            ResultSet rs = ps.executeQuery();

            resultArea.setText(""); // clear old output

            if (rs.next()) {
                appendBold("TRACKING ID:  ", rs.getString("tracking_id"));
                appendBold("SENDER:  ", rs.getString("sender"));
                appendBold("RECEIVER:  ", rs.getString("receiver"));
                appendBold("SOURCE:  ", rs.getString("source"));
                appendBold("DESTINATION:  ", rs.getString("destination"));
                appendBold("WEIGHT:  ", rs.getDouble("weight") + " kg");
                appendBold("CURRENT STATUS:  ", rs.getString("status"));
            } else {
                resultArea.setText("Tracking ID not found OR does not belong to this user.");
            }

        } catch (SQLException ex) {
            resultArea.setText("Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrackingForm().setVisible(true));
    }
}

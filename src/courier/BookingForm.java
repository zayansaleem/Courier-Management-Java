package courier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingForm extends JFrame {

    // --- Components for User ID Verification ---
    private JPanel userPanel;
    private JTextField userIdField;
    private JButton verifyBtn;
    private JLabel verifiedUserLabel;
    private int currentUserId = -1; // Stores the verified user ID

    // --- Components for Courier Booking ---
    private JPanel bookingPanel;
    private JTextField senderField;
    private JTextField receiverField;
    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField weightField;
    private JButton bookCourierBtn;

    public BookingForm() {
        setTitle("Book Courier");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ----------------------------------------
        // STEP 1: User ID Verification Panel
        // ----------------------------------------
        userPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userPanel.add(new JLabel("Enter Your User ID:"));
        userIdField = new JTextField(15);
        userPanel.add(userIdField);

        verifyBtn = new JButton("Verify ID");
        verifyBtn.addActionListener(this::verifyUser);
        userPanel.add(verifyBtn);

        verifiedUserLabel = new JLabel("Status: Awaiting ID");
        userPanel.add(verifiedUserLabel);
        // --- Clickable Register Link ---
        JLabel registerLink = new JLabel(
                "<html>New User? <a href=''>Register Here</a></html>"
        );
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Redirect to UserForm
                new UserForm().setVisible(true);
                // dispose(); // uncomment if you want to close current form
            }
        });

// add into panel
        userPanel.add(registerLink);



        add(userPanel, BorderLayout.NORTH);

        // ----------------------------------------
        // STEP 2: Booking Details Panel (Initially hidden)
        // ----------------------------------------
        bookingPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sender Field (Will be pre-filled with verified user's name)
        bookingPanel.add(new JLabel("SENDER (YOU):"));
        senderField = new JTextField(20);
        senderField.setEditable(false);
        bookingPanel.add(senderField);

        bookingPanel.add(new JLabel("RECEIVER NAME:"));
        receiverField = new JTextField(20);
        bookingPanel.add(receiverField);

        bookingPanel.add(new JLabel("SOURCE LOCATION:"));
        sourceField = new JTextField(20);
        bookingPanel.add(sourceField);

        bookingPanel.add(new JLabel("DESTINATION LOCATION:"));
        destinationField = new JTextField(20);
        bookingPanel.add(destinationField);

        bookingPanel.add(new JLabel("WEIGHT (kg):"));
        weightField = new JTextField(10);
        bookingPanel.add(weightField);

        bookCourierBtn = new JButton("Book Courier");
        bookCourierBtn.addActionListener(this::bookCourier);
        bookingPanel.add(new JLabel(""));
        bookingPanel.add(bookCourierBtn);

        add(bookingPanel, BorderLayout.CENTER);

        // Initially hide the booking panel
        bookingPanel.setVisible(false);

        setLocationRelativeTo(null);
    }

    // --- Method to verify the User ID (No Change from last version) ---
    private void verifyUser(ActionEvent e) {
        String userIdText = userIdField.getText().trim();
        if (userIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT id, name FROM users WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            int id = Integer.parseInt(userIdText);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                String userName = rs.getString("name");

                verifiedUserLabel.setText("Status: User Verified: " + userName);
                senderField.setText(userName);
                bookingPanel.setVisible(true);
                userIdField.setEditable(false);
                verifyBtn.setEnabled(false);

                JOptionPane.showMessageDialog(this, "User ID " + currentUserId + " verified. You may proceed with booking.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                verifiedUserLabel.setText("Status: ID Not Found!");
                bookingPanel.setVisible(false);
                currentUserId = -1;
                JOptionPane.showMessageDialog(this, "No user found with ID: " + userIdText, "Verification Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Method to book the courier (UPDATED to generate and display Tracking ID) ---
    private void bookCourier(ActionEvent e) {
        if (currentUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please verify your User ID first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Basic Validation
        if (receiverField.getText().isEmpty() || sourceField.getText().isEmpty() ||
                destinationField.getText().isEmpty() || weightField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all booking details.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Generate the Tracking ID before inserting
        String trackingId = DBHelper.generateTrackingId();

        try (Connection con = DBHelper.getConnection()) {

            // Insert statement now includes the tracking_id
            String sql = "INSERT INTO couriers (tracking_id, user_id, sender, receiver, source, destination, weight, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, trackingId);           // <--- INSERTING THE GENERATED ID
            ps.setInt(2, currentUserId);
            ps.setString(3, senderField.getText());
            ps.setString(4, receiverField.getText());
            ps.setString(5, sourceField.getText());
            ps.setString(6, destinationField.getText());
            ps.setDouble(7, Double.parseDouble(weightField.getText()));
            ps.setString(8, "BOOKED");

            int rows = ps.executeUpdate();

            if (rows > 0) {

                // Create a selectable, copy-enabled Tracking ID box
                JTextField trackingField = new JTextField(trackingId);
                trackingField.setEditable(false);
                trackingField.setBorder(null);
                trackingField.setFont(new Font("Arial", Font.BOLD, 14));
                trackingField.setCursor(new Cursor(Cursor.TEXT_CURSOR)); // text cursor

                Object[] message = {
                        "Courier booked successfully!",
                        "Your Tracking ID:",
                        trackingField
                };

                JOptionPane.showMessageDialog(this, message, "Booking Success", JOptionPane.INFORMATION_MESSAGE);

                this.dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Booking failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Weight must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingForm().setVisible(true));
    }
}
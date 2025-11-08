package courier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import courier.models.User; // Import the User model class

public class UserForm extends JFrame {

    // Input fields
    private JTextField idField;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField addressField;
    private JTextArea displayArea; // To display search results or messages

    // Buttons
    private JButton registerBtn;
    private JButton searchBtn;
    private JButton updateBtn;
    private JButton clearBtn;

    public UserForm() {
        setTitle("User Management");
        setSize(500, 400); // Increased size for more fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this form
        setLayout(new BorderLayout());

        // --- North Panel: Input Fields ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // 5 rows, 2 columns, with gaps
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("User ID:"));
        idField = new JTextField(15);
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Contact:"));
        contactField = new JTextField(20);
        inputPanel.add(contactField);

        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField(20);
        inputPanel.add(addressField);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center Panel: Display Area ---
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- South Panel: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered buttons with gaps

        registerBtn = new JButton("Register User");
        registerBtn.addActionListener(this::registerUser);
        buttonPanel.add(registerBtn);

        searchBtn = new JButton("Search User");
        searchBtn.addActionListener(this::searchUser);
        buttonPanel.add(searchBtn);

        updateBtn = new JButton("Update User");
        updateBtn.addActionListener(this::updateUser);
        buttonPanel.add(updateBtn);

        clearBtn = new JButton("Clear Fields");
        clearBtn.addActionListener(e -> clearFields());
        buttonPanel.add(clearBtn);


        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the window
    }

    // --- Action Listeners / Methods for Buttons ---

    private void registerUser(ActionEvent e) {
        // Basic validation
        if (idField.getText().isEmpty() || nameField.getText().isEmpty() ||
                contactField.getText().isEmpty() || addressField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields to register a user.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql = "INSERT INTO users (id, name, contact, address) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setString(3, contactField.getText());
            ps.setString(4, addressField.getText());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                displayMessage("User '" + nameField.getText() + "' registered successfully!");
                clearFields();
            } else {
                displayMessage("Failed to register user.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            displayMessage("Database error during registration: " + ex.getMessage());
            // In a real application, log the full stack trace
        }
    }

    private void searchUser(ActionEvent e) {
        String searchId = idField.getText();
        String searchName = nameField.getText();

        if (searchId.isEmpty() && searchName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID or Name to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql;
            PreparedStatement ps;
            if (!searchId.isEmpty()) {
                sql = "SELECT id, name, contact, address FROM users WHERE id = ?";
                ps = con.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchId));
            } else { // Search by name
                sql = "SELECT id, name, contact, address FROM users WHERE name LIKE ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, "%" + searchName + "%"); // Partial name search
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Populate fields with found user data
                idField.setText(String.valueOf(rs.getInt("id")));
                nameField.setText(rs.getString("name"));
                contactField.setText(rs.getString("contact"));
                addressField.setText(rs.getString("address"));
                displayMessage("User found:\n" +
                        "ID: " + rs.getInt("id") + "\n" +
                        "Name: " + rs.getString("name") + "\n" +
                        "Contact: " + rs.getString("contact") + "\n" +
                        "Address: " + rs.getString("address"));
            } else {
                displayMessage("No user found with the given ID/Name.");
                clearFields(); // Clear fields if no user found
                idField.setText(searchId); // Retain the search ID if searching by ID
                nameField.setText(searchName); // Retain the search name if searching by name
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            displayMessage("Database error during search: " + ex.getMessage());
        }
    }

    private void updateUser(ActionEvent e) {
        // Basic validation
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID is required to update a user.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nameField.getText().isEmpty() || contactField.getText().isEmpty() || addressField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill Name, Contact, and Address fields for update.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql = "UPDATE users SET name = ?, contact = ?, address = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nameField.getText());
            ps.setString(2, contactField.getText());
            ps.setString(3, addressField.getText());
            ps.setInt(4, Integer.parseInt(idField.getText()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                displayMessage("User ID " + idField.getText() + " updated successfully!");
                // Optionally clear fields or re-display updated info
            } else {
                displayMessage("No user found with ID " + idField.getText() + " to update, or no changes made.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            displayMessage("Database error during update: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        addressField.setText("");
        displayArea.setText("");
    }

    private void displayMessage(String message) {
        displayArea.setText(message);
    }

    // Main method for testing this form directly (optional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserForm().setVisible(true);
        });
    }
}
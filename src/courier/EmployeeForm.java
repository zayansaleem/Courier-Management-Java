package courier;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EmployeeForm extends JFrame {
    JTextField idField, statusField;
    JButton updateBtn;
    public EmployeeForm() {
        setTitle("Update Courier Status");
        setSize(400,200);
        setLayout(new GridLayout(3,2));
        add(new JLabel("Tracking ID:")); idField = new JTextField(); add(idField);
        add(new JLabel("New Status:")); statusField = new JTextField(); add(statusField);
        updateBtn = new JButton("Update"); add(new JLabel("")); add(updateBtn);
        updateBtn.addActionListener(_ -> update());
    }

    void update() {
        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE couriers SET status=? WHERE tracking_id=?")) {
            ps.setString(1, statusField.getText());
            ps.setString(2, idField.getText());
            int rows = ps.executeUpdate();
            if (rows > 0) JOptionPane.showMessageDialog(this, "Status Updated!");
            else JOptionPane.showMessageDialog(this, "Tracking ID not found.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}


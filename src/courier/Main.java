package courier;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public Main() {
        setTitle("Courier Tracking System");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3,1));

        JButton book = new JButton("BOOK COURIER");
        JButton track = new JButton("TRACK COURIER");
        JButton emp = new JButton("EMPLOYEE UPDATE");
        JButton userBtn = new JButton("MANAGE USERS");
        JButton historyBtn = new JButton("MANAGE HISTORY"); // ✅ new button

        add(book);
        add(track);
        add(emp);
        add(userBtn);
        add(historyBtn); // ✅ added to layout

        book.addActionListener(_ -> new BookingForm().setVisible(true));
        track.addActionListener(_ -> new TrackingForm().setVisible(true));
        emp.addActionListener(_ -> new EmployeeForm().setVisible(true));
        userBtn.addActionListener(_ -> new UserForm().setVisible(true));
        historyBtn.addActionListener(_ -> new HistoryForm().setVisible(true)); // ✅ opens history page

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}


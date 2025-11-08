package courier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {

    // Database connection string for SQLite
    private static final String DB_URL = "jdbc:sqlite:courier.db";

    // Static block to load the SQLite driver and initialize the database (create tables)
    static {
        try {
            // Load the SQLite driver
            Class.forName("org.sqlite.JDBC");
            // Initialize database tables
            init();
        } catch (Exception e) {
            // This catches ClassNotFoundException (if JDBC is missing) and SQLException from init()
            System.err.println("Database Initialization Error: Could not load driver or create tables.");
            e.printStackTrace();
        }
    }

    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        // Returns a new connection instance for each call
        return DriverManager.getConnection(DB_URL);
    }

    // Method to initialize the database tables
    public static void init() {
        // Use try-with-resources to ensure Connection and Statement are closed
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {

            // 1. CREATE users table (Required for User Management and linking couriers)
            // INTEGER PRIMARY KEY is automatically auto-incrementing in SQLite
            s.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "contact TEXT, " +
                    "address TEXT" +
                    ")");

            // 2. CREATE employees table (Required for Employee Update)
            s.execute("CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "position TEXT, " +
                    "contact TEXT" + // Assuming contact is useful for employee details
                    ")");

            // 3. CREATE couriers table (UPDATED: includes user_id column)
            s.execute("CREATE TABLE IF NOT EXISTS couriers (" +
                    "tracking_id TEXT PRIMARY KEY, " + // Used for tracking, e.g., "C" + timestamp
                    "user_id INTEGER NOT NULL, " +     // Foreign Key to users.id
                    "sender TEXT NOT NULL, " +
                    "receiver TEXT, " +
                    "source TEXT, " +
                    "destination TEXT, " +
                    "weight REAL, " +
                    "status TEXT DEFAULT 'Booked', " +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" + // Enforces data integrity
                    ")");

            System.out.println("Database tables checked/created successfully.");

        } catch (SQLException e) {
            System.err.println("SQL Error during database initialization (Table creation failed):");
            e.printStackTrace();
        }
    }

    // Method to generate a unique tracking ID
    public static String generateTrackingId() {
        return "C" + System.currentTimeMillis();
    }

    // Main method for initial testing (optional)
    public static void main(String[] args) {
        // Just calling a method that is already in the static block is sufficient
        System.out.println("DBHelper initialized.");
    }
}
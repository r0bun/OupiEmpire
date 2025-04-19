package database;

import java.sql.*;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/oupi_goupi"; // Change 'your_database'
    private static final String USER = "root"; // Default XAMPP user
    private static final String PASSWORD = ""; // Default XAMPP password (empty)

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = connect()) {
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
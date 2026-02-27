package com.example.javafxh2app.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.*;

public class DB {
    private static final String DB_DIR = "data";
    private static final String URL = "jdbc:h2:./" + DB_DIR + "/appdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void init() {
        try {
            Path dir = Path.of(DB_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            try (Connection c = getConnection();
                 Statement s = c.createStatement()) {

                s.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id IDENTITY PRIMARY KEY,
                        username VARCHAR(100) UNIQUE NOT NULL,
                        password_hash VARCHAR(256) NOT NULL
                    );
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id IDENTITY PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        text VARCHAR(2000),
                        status BOOLEAN DEFAULT FALSE,
                        priority VARCHAR(10) DEFAULT 'Medium',
                        deadline TIMESTAMP,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    );
                """);

                try (PreparedStatement ps = c.prepareStatement(
                        "MERGE INTO users (username, password_hash) KEY(username) VALUES (?, ?)")) {
                    ps.setString(1, "admin");
                    ps.setString(2, hash("1234"));
                    ps.executeUpdate();
                    System.out.println("Admin created/updated, hash: " + hash("admin"));
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte bb : b) sb.append(String.format("%02x", bb));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

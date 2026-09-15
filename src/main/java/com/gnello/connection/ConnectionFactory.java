package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = System.getenv("SUPABASE_DB_URL");
    private static final String USER = System.getenv("SUPABASE_DB_USER");
    private static final String PASSWORD = System.getenv("SUPABASE_DB_PASSWORD");

    public static Connection getConnection() {
        System.out.println("DEBUG URL: " + URL);
        System.out.println("DEBUG USER: " + USER);
        System.out.println("DEBUG PASSWORD SET: " + (PASSWORD != null && !PASSWORD.isEmpty()));
        
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao conectar: " + e.getMessage(), e);
        }
    }
}
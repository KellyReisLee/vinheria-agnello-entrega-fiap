package com.agnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexões com o PostgreSQL (Neon).
 *
 * As credenciais vêm de variáveis de ambiente (nunca do código):
 *   NEON_DB_URL       ex.: jdbc:postgresql://ep-xxx.neon.tech/neondb?sslmode=require
 *   NEON_DB_USER
 *   NEON_DB_PASSWORD
 *
 * A ligação é sempre cifrada (TLS): se a URL não disser nada, usamos sslmode=require,
 * para os dados não viajarem em texto puro entre o servidor e o banco.
 */
public final class ConnectionFactory {

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        String url = System.getenv("NEON_DB_URL");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("A variável de ambiente NEON_DB_URL não está configurada.");
        }

        Properties props = new Properties();
        String user = System.getenv("NEON_DB_USER");
        String password = System.getenv("NEON_DB_PASSWORD");
        if (user != null) {
            props.setProperty("user", user);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        if (!url.contains("sslmode=")) {
            props.setProperty("sslmode", "require");
        }

        try {
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível conectar ao banco de dados.", e);
        }
    }
}

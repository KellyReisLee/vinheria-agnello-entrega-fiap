package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            String url = System.getenv("SUPABASE_DB_URL");
            
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("A variável de ambiente SUPABASE_DB_URL não está configurada!");
            }
            
            // Limpa caso venha com prefixos duplicados acidentais
            url = url.replace("jdbc:jdbc:postgresql://", "jdbc:postgresql://");
            url = url.replace("postgresql://postgresql://", "postgresql://");
            
            // Garante o prefixo correto apenas uma vez
            if (!url.startsWith("jdbc:postgresql://")) {
                if (url.startsWith("postgresql://")) {
                    url = "jdbc:" + url;
                } else {
                    url = "jdbc:postgresql://" + url;
                }
            }
            
            return DriverManager.getConnection(url);
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados no Neon!", e);
        }
    }
}
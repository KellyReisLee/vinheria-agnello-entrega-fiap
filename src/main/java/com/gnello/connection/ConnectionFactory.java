package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            // Pega a URL completa da variável de ambiente
            String url = System.getenv("SUPABASE_DB_URL");
            
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("A variável de ambiente SUPABASE_DB_URL não está configurada!");
            }
            
            // Garante que o prefixo jdbc: esteja presente para o driver do Postgres reconhecer
            if (!url.startsWith("jdbc:")) {
                url = "jdbc:" + url;
            }
            
            // Como a URL do Neon já inclui usuário e senha, usamos a sobrecarga de 1 parâmetro
            return DriverManager.getConnection(url);
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados no Neon!", e);
        }
    }
}
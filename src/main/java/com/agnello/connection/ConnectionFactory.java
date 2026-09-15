package com.agnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            // URL limpa buscada da variável de ambiente do Render
            String url = System.getenv("SUPABASE_DB_URL");
            String user = System.getenv("SUPABASE_DB_USER");
            String password = System.getenv("SUPABASE_DB_PASSWORD");
            
            // Validação opcional para garantir que o ambiente está configurado
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("A variável de ambiente SUPABASE_DB_URL não está configurada!");
            }
            
            // Passando as credenciais separadamente (exatamente como funcionou no seu teste), 
            // mas agora vindas das variáveis de ambiente com total segurança
            Properties props = new Properties();
            if (user != null) {
                props.setProperty("user", user);
            }
            if (password != null) {
                props.setProperty("password", password);
            }
            
            return DriverManager.getConnection(url, props);
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados no Neon!", e);
        }
    }
}
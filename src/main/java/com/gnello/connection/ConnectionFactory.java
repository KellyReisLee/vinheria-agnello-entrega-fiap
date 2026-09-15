package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            // URL limpa apenas com o endereço e o SSL, sem usuário/senha na string
            String url = "jdbc:postgresql://ep-cold-boat-b51gyhxk-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require";
            
            // Passando as credenciais separadamente para evitar o bug do driver antigo
            Properties props = new Properties();
            props.setProperty("user", "neondb_owner");
            props.setProperty("password", "npg_kJ1bN0ArPGFu");
            
            return DriverManager.getConnection(url, props);
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados no Neon!", e);
        }
    }
}
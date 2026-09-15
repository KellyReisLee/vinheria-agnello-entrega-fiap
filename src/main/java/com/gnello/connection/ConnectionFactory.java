package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            // Forçamos a URL completa do JDBC aqui diretamente para isolar o problema
            String url = "jdbc:postgresql://neondb_owner:npg_kJ1bN0ArPGFu@ep-cold-boat-b51gyhxk-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
            
            return DriverManager.getConnection(url);
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados no Neon!", e);
        }
    }
}
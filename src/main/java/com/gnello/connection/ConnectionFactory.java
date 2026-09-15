package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	private static final String URL = System.getenv("SUPABASE_DB_URL");
	private static final String USER = System.getenv("SUPABASE_DB_USER");
	// Pega a senha da variável de ambiente do sistema
	private static final String PASSWORD = System.getenv("SUPABASE_DB_PASSWORD");

	public static Connection getConnection() {
	    try {
	        Class.forName("org.postgresql.Driver");
	        return DriverManager.getConnection(URL, USER, PASSWORD);
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
	    } catch (SQLException e) {
	        // Imprime o erro técnico real no log do Render
	        e.printStackTrace();
	        throw new RuntimeException("Erro ao conectar com o banco: " + e.getMessage(), e);
	    }
	}
}

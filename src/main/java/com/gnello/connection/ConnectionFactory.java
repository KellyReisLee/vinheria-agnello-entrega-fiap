package com.gnello.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	private static final String URL = "jdbc:postgresql://db.nllntgowozapqdysntlk.supabase.co:5432/postgres";
	private static final String USER = "postgres";
	// Pega a senha da variável de ambiente do sistema
	private static final String PASSWORD = System.getenv("SUPABASE_DB_PASSWORD");

	public static Connection getConnection() {
	    try {
	        Class.forName("org.postgresql.Driver");
	        return DriverManager.getConnection(URL, USER, PASSWORD);
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException("Erro: Driver JDBC do PostgreSQL não encontrado nas bibliotecas!", e);
	    } catch (SQLException e) {
	        throw new RuntimeException("Erro ao conectar com o banco de dados no Supabase!", e);
	    }
	}
}
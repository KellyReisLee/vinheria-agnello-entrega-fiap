package com.agnello.dao;

import com.agnello.connection.ConnectionFactory;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;
import com.agnello.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    public void cadastrarPF(ClientePF cliente) {
        String sql = "INSERT INTO cliente (tipo_cliente, nome, sobrenome, cpf, email, telefone, senha) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "PF");
            stmt.setString(2, cliente.getNome());
            stmt.setString(3, cliente.getSobrenome());
            stmt.setString(4, cliente.getCpf());
            stmt.setString(5, cliente.getEmail());
            stmt.setString(6, cliente.getTelefone());
            stmt.setString(7, cliente.getSenha());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar Cliente PF no Supabase: " + e.getMessage(), e);
        }
    }

    public void cadastrarPJ(ClientePJ cliente) {
        String sql = "INSERT INTO cliente (tipo_cliente, razao_social, cnpj, email, telefone, senha) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "PJ");
            stmt.setString(2, cliente.getRazaoSocial());
            stmt.setString(3, cliente.getCnpj());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getTelefone());
            stmt.setString(6, cliente.getSenha());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar Cliente PJ no Supabase: " + e.getMessage(), e);
        }
    }
    
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM cliente WHERE email = ?";
        Usuario usuario = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo_cliente");
                    int id = rs.getInt("id");
                    String senha = rs.getString("senha");
                    String telefone = rs.getString("telefone");

                    if ("PF".equals(tipo)) {
                        ClientePF pf = new ClientePF();
                        pf.setId(id);
                        pf.setEmail(email);
                        pf.setSenha(senha);
                        pf.setTelefone(telefone);
                        pf.setNome(rs.getString("nome"));
                        pf.setSobrenome(rs.getString("sobrenome"));
                        pf.setCpf(rs.getString("cpf"));
                        usuario = pf;
                    } else if ("PJ".equals(tipo)) {
                        ClientePJ pj = new ClientePJ();
                        pj.setId(id);
                        pj.setEmail(email);
                        pj.setSenha(senha);
                        pj.setTelefone(telefone);
                        pj.setRazaoSocial(rs.getString("razao_social"));
                        pj.setCnpj(rs.getString("cnpj"));
                        usuario = pj;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por e-mail: " + e.getMessage(), e);
        }

        return usuario;
    }
    
    
    public void atualizarSenhaPorEmail(String email, String novaSenha) {
        String sql = "UPDATE cliente SET senha = ? WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novaSenha);
            stmt.setString(2, email);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum cliente foi encontrado com o e-mail: " + email);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar a senha no banco de dados: " + e.getMessage(), e);
        }
    }
    
    
}
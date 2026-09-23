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
        String sql = "INSERT INTO clientes (tipo_cliente, nome, sobrenome, cpf, email, telefone, senha) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
        String sql = "INSERT INTO clientes (tipo_cliente, razao_social, cnpj, email, telefone, senha) VALUES (?, ?, ?, ?, ?, ?)";

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
        String sql = "SELECT * FROM clientes WHERE email = ?";
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
        String sql = "UPDATE clientes SET senha = ? WHERE email = ?";

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
    
    
    /**
     * Remove o cliente da base de dados com base no e-mail.
     */
    public boolean deletarPorEmail(String email) throws SQLException {
    	String sql = "DELETE FROM clientes WHERE email = ?";
        
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            int linhasAfetadas = stmt.executeUpdate();
            
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            throw new SQLException("Erro crítico ao tentar remover o utilizador do banco de dados.", e);
        }
    }
    
    /**
     * Salva o token de recuperação e a data de expiração para o e-mail correspondente.
     */
    public void salvarTokenRecuperacao(String email, String token, java.time.LocalDateTime expiracao) {
        String sql = "UPDATE clientes SET token_recuperacao = ?, token_expiracao = ? WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(expiracao));
            stmt.setString(3, email);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o token de recuperação: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorTokenRecuperacao(String token) {
        // Atenção ao nome correto da tabela ("clientes")
        String sql = "SELECT * FROM clientes WHERE token_recuperacao = ? AND token_expiracao > ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Descobre se é PF ou PJ com base numa coluna da base de dados (ex: 'tipo')
                    String tipoCliente = rs.getString("tipo_cliente"); 
                    
                    Usuario usuario;
                    if ("PJ".equalsIgnoreCase(tipoCliente)) {
                        ClientePJ pj = new ClientePJ();
                        pj.setRazaoSocial(rs.getString("razao_social"));
                        pj.setCnpj(rs.getString("cnpj"));
                        usuario = pj;
                    } else {
                        ClientePF pf = new ClientePF();
                        pf.setNome(rs.getString("nome"));
                        pf.setSobrenome(rs.getString("sobreNome"));
                        pf.setCpf(rs.getString("cpf"));
                        usuario = pf;
                    }
                    
                    // Preenche os campos comuns da classe mãe Usuario
                    usuario.setId(rs.getInt("id"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setTelefone(rs.getString("telefone"));
                    usuario.setEmailVerificado(rs.getBoolean("email_verificado"));
                    usuario.setTokenAtivacao(rs.getString("token_ativacao"));
                    usuario.setTokenRecuperacao(rs.getString("token_recuperacao"));
                    
                    java.sql.Timestamp timestampExp = rs.getTimestamp("token_expiracao");
                    if (timestampExp != null) {
                        usuario.setTokenExpiracao(timestampExp.toLocalDateTime());
                    }
                    
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar utilizador por token de recuperação: " + e.getMessage(), e);
        }
        return null;
    }
    
    
    /**
     * Valida se o token existe e ainda está dentro do prazo de 30 minutos.
     */
    public boolean validarToken(String token) {
        String sql = "SELECT id FROM clientes WHERE token_recuperacao = ? AND token_expiracao > ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou um registo válido e não expirado
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao validar o token de recuperação: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza a senha do cliente usando o token e logo em seguida limpa os campos 
     * do token (invalidando-o para uso único).
     */
    public void atualizarSenhaEInvalidarToken(String token, String novaSenha) {
        String sql = "UPDATE clientes SET senha = ?, token_recuperacao = NULL, token_expiracao = NULL WHERE token_recuperacao = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novaSenha);
            stmt.setString(2, token);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new RuntimeException("Token inválido ou já utilizado.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar a senha e invalidar o token: " + e.getMessage(), e);
        }
    }
    
}
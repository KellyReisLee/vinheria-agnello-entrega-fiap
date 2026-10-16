package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import com.agnello.connection.ConnectionFactory;
import com.agnello.exception.DadoDuplicadoException;
import com.agnello.model.Administrador;

/** Acesso à tabela "administradores". */
public class AdministradorDAO {

    private static final String COLUNAS = "id, nome, email, senha_hash, ativo, bloqueado_ate";

    public Administrador buscarPorEmail(String email) {
        return buscar("SELECT " + COLUNAS + " FROM administradores WHERE lower(email) = lower(?)", email);
    }

    public Administrador buscarPorId(int id) {
        return buscar("SELECT " + COLUNAS + " FROM administradores WHERE id = ?", id);
    }

    /** Cria a conta e devolve o id. Usado pelo programa CriarAdministrador. */
    public int inserir(String nome, String email, String senhaHash) throws DadoDuplicadoException {
        String sql = "INSERT INTO administradores (nome, email, senha_hash) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senhaHash);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // violação de unicidade
                throw new DadoDuplicadoException("Já existe um administrador com este e-mail.");
            }
            throw new RuntimeException("Erro ao criar administrador", e);
        }
    }

    /** Troca a senha (usado pelo CriarAdministrador quando o e-mail já existe). */
    public void atualizarSenha(int id, String senhaHash) {
        executar("UPDATE administradores SET senha_hash = ?, tentativas_login_falhas = 0, bloqueado_ate = NULL,"
                + " ativo = TRUE WHERE id = ?", senhaHash, id);
    }

    /** Mesma regra dos clientes: 5 erros seguidos bloqueiam a conta por alguns minutos. */
    public void registrarFalhaLogin(int id, int maxTentativas, int minutosBloqueio) {
        executar("UPDATE administradores SET "
                + "tentativas_login_falhas = CASE WHEN tentativas_login_falhas + 1 >= ? THEN 0 ELSE tentativas_login_falhas + 1 END, "
                + "bloqueado_ate = CASE WHEN tentativas_login_falhas + 1 >= ? THEN now() + make_interval(mins => ?) ELSE bloqueado_ate END "
                + "WHERE id = ?", maxTentativas, maxTentativas, minutosBloqueio, id);
    }

    public void registrarLoginComSucesso(int id, String novoHashOuNull) {
        executar("UPDATE administradores SET tentativas_login_falhas = 0, bloqueado_ate = NULL, ultimo_login_em = now(), "
                + "senha_hash = COALESCE(?, senha_hash) WHERE id = ?", novoHashOuNull, id);
    }

    // ------------------------------------------------------------------ auxiliares

    private Administrador buscar(String sql, Object parametro) {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Administrador a = new Administrador();
                a.setId(rs.getInt("id"));
                a.setNome(rs.getString("nome"));
                a.setEmail(rs.getString("email"));
                a.setSenhaHash(rs.getString("senha_hash"));
                a.setAtivo(rs.getBoolean("ativo"));
                a.setBloqueadoAte(rs.getObject("bloqueado_ate", OffsetDateTime.class));
                return a;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar administrador", e);
        }
    }

    private void executar(String sql, Object... parametros) {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar administrador", e);
        }
    }
}

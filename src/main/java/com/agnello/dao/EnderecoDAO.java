package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.agnello.connection.ConnectionFactory;
import com.agnello.model.Endereco;
import com.agnello.seguranca.CifraDados;

/**
 * Endereços da conta do cliente (tabela "enderecos").
 *
 * O endereço é dado pessoal: os campos são juntados num texto só e cifrados
 * (AES-256-GCM). No banco, só o apelido ("Casa") fica legível.
 *
 * TODAS as consultas filtram também pelo cliente_id: assim um cliente nunca
 * consegue ler, mudar ou apagar o endereço de outro, mesmo trocando o id na URL.
 */
public class EnderecoDAO {

    private static final String CAMPO = "endereco_salvo";
    /** Separador entre os campos dentro do texto cifrado (um caractere que ninguém digita). */
    private static final String SEP = "\u001F";

    public List<Endereco> listar(int clienteId) {
        String sql = "SELECT id, apelido, dados_cifrados, principal FROM enderecos WHERE cliente_id = ?"
                + " ORDER BY principal DESC, id";
        List<Endereco> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar endereços", e);
        }
        return lista;
    }

    public int contar(int clienteId) {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM enderecos WHERE cliente_id = ?")) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar endereços", e);
        }
    }

    /** Grava um endereço novo. Se for o primeiro (ou marcado principal), vira o principal. */
    public int inserir(int clienteId, Endereco e) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean principal = e.isPrincipal() || contar(conn, clienteId) == 0;
                if (principal) {
                    limparPrincipal(conn, clienteId);
                }
                int id;
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO enderecos"
                        + " (cliente_id, apelido, dados_cifrados, principal) VALUES (?, ?, ?, ?) RETURNING id")) {
                    stmt.setInt(1, clienteId);
                    stmt.setString(2, e.getApelido());
                    stmt.setString(3, cifrar(e));
                    stmt.setBoolean(4, principal);
                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        id = rs.getInt(1);
                    }
                }
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao gravar endereço", ex);
        }
    }

    public boolean excluir(int clienteId, int id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean eraPrincipal;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM enderecos WHERE id = ? AND cliente_id = ? RETURNING principal")) {
                    stmt.setInt(1, id);
                    stmt.setInt(2, clienteId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        eraPrincipal = rs.getBoolean(1);
                    }
                }
                // Apagou o principal: o endereço mais antigo que sobrou passa a ser o principal
                if (eraPrincipal) {
                    try (PreparedStatement stmt = conn.prepareStatement("UPDATE enderecos SET principal = TRUE"
                            + " WHERE id = (SELECT min(id) FROM enderecos WHERE cliente_id = ?)")) {
                        stmt.setInt(1, clienteId);
                        stmt.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao excluir endereço", ex);
        }
    }

    public boolean tornarPrincipal(int clienteId, int id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                limparPrincipal(conn, clienteId);
                int alterados;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE enderecos SET principal = TRUE WHERE id = ? AND cliente_id = ?")) {
                    stmt.setInt(1, id);
                    stmt.setInt(2, clienteId);
                    alterados = stmt.executeUpdate();
                }
                if (alterados == 0) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao definir endereço principal", ex);
        }
    }

    // ------------------------------------------------------------------ auxiliares

    private static int contar(Connection conn, int clienteId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM enderecos WHERE cliente_id = ?")) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static void limparPrincipal(Connection conn, int clienteId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE enderecos SET principal = FALSE WHERE cliente_id = ? AND principal")) {
            stmt.setInt(1, clienteId);
            stmt.executeUpdate();
        }
    }

    private static String cifrar(Endereco e) {
        String texto = String.join(SEP, e.getCep(), e.getLogradouro(), e.getNumero(),
                e.getComplemento() == null ? "" : e.getComplemento(), e.getBairro(), e.getCidade());
        return CifraDados.cifrar(CAMPO, texto);
    }

    private static Endereco mapear(ResultSet rs) throws SQLException {
        Endereco e = new Endereco();
        e.setId(rs.getInt("id"));
        e.setApelido(rs.getString("apelido"));
        e.setPrincipal(rs.getBoolean("principal"));
        String[] p = CifraDados.decifrar(CAMPO, rs.getString("dados_cifrados")).split(SEP, -1);
        e.setCep(p[0]);
        e.setLogradouro(p[1]);
        e.setNumero(p[2]);
        e.setComplemento(p[3].isEmpty() ? null : p[3]);
        e.setBairro(p[4]);
        e.setCidade(p[5]);
        return e;
    }
}

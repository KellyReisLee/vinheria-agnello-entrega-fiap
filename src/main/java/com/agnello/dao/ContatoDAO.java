package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.agnello.connection.ConnectionFactory;
import com.agnello.model.MensagemContato;

/** Acesso à tabela "mensagens_contato". */
public class ContatoDAO {

    /** Grava a mensagem e devolve o id gerado. */
    public int inserir(MensagemContato m) {
        String sql = "INSERT INTO mensagens_contato (cliente_id, nome, email, assunto, numero_pedido, mensagem)"
                + " VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (m.getClienteId() == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, m.getClienteId());
            }
            stmt.setString(2, m.getNome());
            stmt.setString(3, m.getEmail());
            stmt.setString(4, m.getAssunto());
            stmt.setString(5, m.getNumeroPedido());
            stmt.setString(6, m.getMensagem());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gravar a mensagem de contato", e);
        }
    }

    // ================================================================== painel administrativo

    private static final String SELECT_PAINEL = "SELECT m.id, m.cliente_id, m.nome, m.email, m.assunto,"
            + " m.numero_pedido, m.mensagem, m.criado_em, m.respondida, m.resposta, m.respondida_em,"
            + " a.nome AS nome_admin FROM mensagens_contato m"
            + " LEFT JOIN administradores a ON a.id = m.respondida_por";

    /** @param pendentes true = só as sem resposta; false = todas */
    public List<MensagemContato> listar(boolean pendentes, int limite) {
        String sql = SELECT_PAINEL + (pendentes ? " WHERE NOT m.respondida" : "")
                + " ORDER BY m.respondida, m.criado_em DESC LIMIT ?";
        List<MensagemContato> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar mensagens de contato", e);
        }
        return lista;
    }

    public MensagemContato buscarPorId(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_PAINEL + " WHERE m.id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a mensagem id=" + id, e);
        }
    }

    public int contarPendentes() {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT count(*) FROM mensagens_contato WHERE NOT respondida");
                ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar mensagens pendentes", e);
        }
    }

    /**
     * Guarda a resposta. "AND NOT respondida" impede que duas pessoas respondam
     * a mesma mensagem: a segunda recebe false.
     */
    public boolean registrarResposta(int id, String resposta, int administradorId) {
        String sql = "UPDATE mensagens_contato SET respondida = TRUE, resposta = ?, respondida_em = now(),"
                + " respondida_por = ? WHERE id = ? AND NOT respondida";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resposta);
            stmt.setInt(2, administradorId);
            stmt.setInt(3, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar a resposta da mensagem id=" + id, e);
        }
    }

    /** Marca como resolvida sem enviar e-mail (ex.: o cliente já foi atendido pelo WhatsApp). */
    public boolean marcarResolvida(int id, int administradorId) {
        return registrarResposta(id, null, administradorId);
    }

    private MensagemContato mapear(ResultSet rs) throws SQLException {
        MensagemContato m = new MensagemContato();
        m.setId(rs.getInt("id"));
        int clienteId = rs.getInt("cliente_id");
        m.setClienteId(rs.wasNull() ? null : clienteId);
        m.setNome(rs.getString("nome"));
        m.setEmail(rs.getString("email"));
        m.setAssunto(rs.getString("assunto"));
        m.setNumeroPedido(rs.getString("numero_pedido"));
        m.setMensagem(rs.getString("mensagem"));
        m.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
        m.setRespondida(rs.getBoolean("respondida"));
        m.setResposta(rs.getString("resposta"));
        m.setRespondidaEm(rs.getObject("respondida_em", OffsetDateTime.class));
        m.setNomeAdministrador(rs.getString("nome_admin"));
        return m;
    }
}

package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
}

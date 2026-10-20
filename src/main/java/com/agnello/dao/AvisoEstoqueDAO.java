package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.agnello.connection.ConnectionFactory;

/** Tabela avisos_estoque ("Avise-me quando chegar"). */
public class AvisoEstoqueDAO {

    /**
     * Guarda o pedido de aviso. Se a pessoa já tinha pedido para este vinho,
     * não duplica (o índice único ignora o segundo pedido).
     * @return true se foi gravado agora; false se já existia
     */
    public boolean registrar(int produtoId, String email, Integer clienteId) {
        String sql = "INSERT INTO avisos_estoque (produto_id, email, cliente_id) VALUES (?, ?, ?)"
                + " ON CONFLICT (produto_id, email) WHERE avisado_em IS NULL DO NOTHING";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            stmt.setString(2, email);
            if (clienteId == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, clienteId);
            }
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar o aviso de estoque", e);
        }
    }

    /**
     * Marca como avisados (numa só operação) e devolve id → e-mail de quem estava
     * à espera deste vinho. Marcar e ler juntos evita que duas pessoas no painel
     * enviem o mesmo aviso duas vezes.
     */
    public Map<Integer, String> retirarPendentes(int produtoId, int limite) {
        String sql = "UPDATE avisos_estoque SET avisado_em = now() WHERE id IN ("
                + " SELECT id FROM avisos_estoque WHERE produto_id = ? AND avisado_em IS NULL"
                + " ORDER BY criado_em LIMIT ? FOR UPDATE SKIP LOCKED) RETURNING id, email";
        Map<Integer, String> emails = new LinkedHashMap<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            stmt.setInt(2, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.put(rs.getInt("id"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler os avisos do produto id=" + produtoId, e);
        }
        return emails;
    }

    /** Quantas pessoas esperam por cada vinho (para o painel). */
    public Map<Integer, Integer> contarPendentesPorProduto() {
        Map<Integer, Integer> contagem = new HashMap<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT produto_id, count(*) FROM avisos_estoque"
                        + " WHERE avisado_em IS NULL GROUP BY produto_id");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                contagem.put(rs.getInt(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar os avisos de estoque", e);
        }
        return contagem;
    }

    /** Apaga avisos já enviados há mais de 90 dias (o e-mail não fica guardado sem necessidade). */
    public int limparAntigos() {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM avisos_estoque"
                        + " WHERE avisado_em < now() - interval '90 days'")) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar avisos antigos", e);
        }
    }
}

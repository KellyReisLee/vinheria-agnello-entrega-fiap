package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.agnello.connection.ConnectionFactory;
import com.agnello.model.Produto;

/** Vinhos favoritos dos clientes (tabela "favoritos"). */
public class FavoritoDAO {

    /** Ids dos vinhos favoritos (para pintar os corações no catálogo). */
    public Set<Integer> listarIds(int clienteId) {
        Set<Integer> ids = new HashSet<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT produto_id FROM favoritos WHERE cliente_id = ?")) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar favoritos", e);
        }
        return ids;
    }

    /** Os vinhos favoritos (só os ativos), do mais recente para o mais antigo. */
    public List<Produto> listarProdutos(int clienteId) {
        String sql = "SELECT " + prefixar(ProdutoDAO.COLUNAS) + " FROM favoritos f JOIN produtos p ON p.id = f.produto_id"
                + " WHERE f.cliente_id = ? AND p.ativo ORDER BY f.criado_em DESC";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(ProdutoDAO.mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vinhos favoritos", e);
        }
        return lista;
    }

    /** Adiciona (ignora se já existe). Devolve false se o vinho não existir ou estiver inativo. */
    public boolean adicionar(int clienteId, int produtoId) {
        String sql = "INSERT INTO favoritos (cliente_id, produto_id) SELECT ?, id FROM produtos WHERE id = ? AND ativo"
                + " ON CONFLICT DO NOTHING";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, produtoId);
            stmt.executeUpdate();
            return existe(conn, clienteId, produtoId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar favorito", e);
        }
    }

    public void remover(int clienteId, int produtoId) {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM favoritos WHERE cliente_id = ? AND produto_id = ?")) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, produtoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover favorito", e);
        }
    }

    private static boolean existe(Connection conn, int clienteId, int produtoId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM favoritos WHERE cliente_id = ? AND produto_id = ?")) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** "id, nome, …" → "p.id, p.nome, …" (evita ambiguidade com colunas da tabela favoritos). */
    private static String prefixar(String colunas) {
        return "p." + colunas.replace(", ", ", p.");
    }
}

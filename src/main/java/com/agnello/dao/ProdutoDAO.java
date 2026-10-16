package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agnello.connection.ConnectionFactory;
import com.agnello.model.Produto;

/**
 * Acesso à tabela "produtos".
 *
 * Todas as consultas usam a mesma lista de colunas (COLUNAS) e o mesmo método
 * para transformar uma linha do banco num objeto Produto (mapear). Assim, se
 * um dia a tabela ganhar uma coluna, só é preciso mexer em dois lugares.
 */
public class ProdutoDAO {

    static final String COLUNAS =
            "id, nome, tipo, origem, descricao, preco, preco_antigo, desconto, pontuacao, estoque, imagem";

    /** Todos os vinhos, na ordem em que foram cadastrados. */
    public List<Produto> listarTodos() {
        return listar("SELECT " + COLUNAS + " FROM produtos ORDER BY id");
    }

    /** Os 4 vinhos cadastrados mais recentemente (vitrine da home). */
    public List<Produto> listarUltimos4() {
        return listar("SELECT " + COLUNAS + " FROM produtos ORDER BY id DESC LIMIT 4");
    }

    /** Um vinho pelo id, ou null se não existir. */
    public Produto buscarPorId(int id) {
        String sql = "SELECT " + COLUNAS + " FROM produtos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o produto id=" + id, e);
        }
    }

    /**
     * Outros vinhos do mesmo tipo (ex.: "Tinto"), com estoque, sem repetir o
     * próprio vinho. Os mais recentes aparecem primeiro.
     */
    public List<Produto> listarRelacionados(String tipo, int idExcluido, int limite) {
        String sql = "SELECT " + COLUNAS + " FROM produtos"
                + " WHERE lower(tipo) = lower(?) AND id <> ? AND estoque > 0"
                + " ORDER BY id DESC LIMIT ?";
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            stmt.setInt(2, idExcluido);
            stmt.setInt(3, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vinhos relacionados", e);
        }
        return produtos;
    }

    /**
     * Vários vinhos de uma vez (usado no checkout para conferir preço e estoque
     * de todos os itens do carrinho numa única consulta). Chave do mapa = id.
     */
    public Map<Integer, Produto> buscarPorIds(Collection<Integer> ids) {
        Map<Integer, Produto> produtos = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return produtos;
        }
        String sql = "SELECT " + COLUNAS + " FROM produtos WHERE id = ANY (?)";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setArray(1, conn.createArrayOf("integer", ids.toArray()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = mapear(rs);
                    produtos.put(p.getId(), p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por id", e);
        }
        return produtos;
    }

    // ------------------------------------------------------------------ auxiliares

    private List<Produto> listar(String sql) {
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                produtos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
        return produtos;
    }

    /** Transforma a linha atual do ResultSet num Produto. Também usado pelo PedidoDAO. */
    static Produto mapear(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id"));
        produto.setNome(rs.getString("nome"));
        produto.setTipo(rs.getString("tipo"));
        produto.setOrigem(rs.getString("origem"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setPrecoAntigo(rs.getDouble("preco_antigo"));
        produto.setDesconto(rs.getString("desconto"));
        produto.setPontuacao(rs.getString("pontuacao"));
        produto.setEstoque(rs.getInt("estoque"));
        produto.setImagem(rs.getString("imagem"));
        return produto;
    }
}

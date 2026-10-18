package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Types;
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
            "id, nome, tipo, origem, descricao, preco, preco_antigo, desconto, pontuacao, estoque, imagem, ativo";

    /** Vinhos à venda (ativos), na ordem em que foram cadastrados. */
    public List<Produto> listarTodos() {
        return listar("SELECT " + COLUNAS + " FROM produtos WHERE ativo ORDER BY id");
    }

    /** Os 4 vinhos ativos cadastrados mais recentemente (vitrine da home). */
    public List<Produto> listarUltimos4() {
        return listar("SELECT " + COLUNAS + " FROM produtos WHERE ativo ORDER BY id DESC LIMIT 4");
    }

    /** Um vinho pelo id (ativo ou não), ou null se não existir. Quem decide se pode ser mostrado é o Service. */
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
                + " WHERE lower(tipo) = lower(?) AND id <> ? AND estoque > 0 AND ativo"
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

    // ================================================================== painel administrativo

    /**
     * Todos os vinhos para o painel (ativos e inativos).
     * @param busca  parte do nome, tipo ou origem (opcional)
     * @param ativos true = só ativos, false = só inativos, null = todos
     */
    public List<Produto> listarParaAdmin(String busca, Boolean ativos) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM produtos WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (ativos != null) {
            sql.append(" AND ativo = ?");
            parametros.add(ativos);
        }
        if (busca != null && !busca.isBlank()) {
            sql.append(" AND (nome ILIKE ? OR tipo ILIKE ? OR origem ILIKE ?)");
            String termo = "%" + busca.trim().replace("%", "").replace("_", "") + "%";
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        sql.append(" ORDER BY ativo DESC, nome");
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos para o painel", e);
        }
        return produtos;
    }

    /** Tipos já usados (ex.: "Tinto Encorpado"), para sugerir no formulário. */
    public List<String> listarTipos() {
        List<String> tipos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT tipo FROM produtos ORDER BY tipo");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tipos.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tipos de vinho", e);
        }
        return tipos;
    }

    /** Vinhos ativos com estoque abaixo do limite (alerta do painel). */
    public int contarEstoqueBaixo(int limite) {
        String sql = "SELECT count(*) FROM produtos WHERE ativo AND estoque < ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar estoque baixo", e);
        }
    }

    /** Grava um vinho novo e devolve o id. */
    public int inserir(Produto p) {
        String sql = "INSERT INTO produtos (nome, tipo, origem, descricao, preco, preco_antigo, desconto, pontuacao,"
                + " estoque, imagem, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, p);
            stmt.setBoolean(11, p.isAtivo());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar o produto", e);
        }
    }

    /** Atualiza todos os campos de um vinho. Devolve false se o id não existir. */
    public boolean atualizar(Produto p) {
        String sql = "UPDATE produtos SET nome = ?, tipo = ?, origem = ?, descricao = ?, preco = ?, preco_antigo = ?,"
                + " desconto = ?, pontuacao = ?, estoque = ?, imagem = ?, ativo = ?, atualizado_em = now() WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, p);
            stmt.setBoolean(11, p.isAtivo());
            stmt.setInt(12, p.getId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o produto id=" + p.getId(), e);
        }
    }

    public boolean definirAtivo(int id, boolean ativo) {
        return executarUpdate("UPDATE produtos SET ativo = ?, atualizado_em = now() WHERE id = ?", ativo, id);
    }

    public boolean definirEstoque(int id, int estoque) {
        return executarUpdate("UPDATE produtos SET estoque = ?, atualizado_em = now() WHERE id = ?", estoque, id);
    }

    private void preencher(PreparedStatement stmt, Produto p) throws SQLException {
        stmt.setString(1, p.getNome());
        stmt.setString(2, p.getTipo());
        stmt.setString(3, p.getOrigem());
        stmt.setString(4, p.getDescricao());
        stmt.setBigDecimal(5, BigDecimal.valueOf(p.getPreco()));
        if (p.getPrecoAntigo() > 0) {
            stmt.setBigDecimal(6, BigDecimal.valueOf(p.getPrecoAntigo()));
        } else {
            stmt.setNull(6, Types.NUMERIC);
        }
        stmt.setString(7, p.getDesconto());
        stmt.setString(8, p.getPontuacao());
        stmt.setInt(9, p.getEstoque());
        stmt.setString(10, p.getImagem());
    }

    private boolean executarUpdate(String sql, Object... parametros) {
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
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
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }
}

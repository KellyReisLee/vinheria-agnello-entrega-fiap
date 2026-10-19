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
import com.agnello.model.Pagina;
import com.agnello.model.Produto;
import com.agnello.model.FiltroCatalogo;
import com.agnello.util.Texto;

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

    // ================================================================== catálogo com filtros

    /** Texto sem acentos e em minúsculas, feito pelo próprio PostgreSQL ("Rosé" → "rose"). */
    private static String semAcento(String coluna) {
        return "translate(lower(COALESCE(" + coluna + ", '')), 'áàâãäéèêëíìîïóòôõöúùûüçñ', 'aaaaaeeeeiiiiooooouuuucn')";
    }

    /** País = o que vem depois da última vírgula da origem ("Mendoza, Argentina" → "Argentina"). */
    private static final String PAIS = "trim(regexp_replace(COALESCE(origem, ''), '^.*,', ''))";

    /** Pedaços de SQL FIXOS para cada tipo (escolhidos por uma lista fechada, nunca pelo texto do usuário). */
    private static final java.util.Map<String, String> SQL_TIPO = java.util.Map.of(
            "tinto", semAcento("tipo") + " LIKE '%tinto%'",
            "branco", "(" + semAcento("tipo") + " LIKE '%branco%' AND " + semAcento("tipo") + " NOT LIKE '%espumante%')",
            "rose", semAcento("tipo") + " LIKE '%rose%'",
            "espumante", "(" + semAcento("tipo") + " LIKE '%espumante%' OR " + semAcento("tipo") + " LIKE '%prosecco%' OR "
                    + semAcento("tipo") + " LIKE '%cava%' OR " + semAcento("tipo") + " LIKE '%champagne%')");

    private static final String PONTOS = "NULLIF(substring(COALESCE(pontuacao, '') from '^[0-9]+'), '')::int";

    /** Ordenações FIXAS; esgotados vão sempre para o fim. */
    private static final java.util.Map<String, String> SQL_ORDEM = java.util.Map.of(
            "relevancia", "id",
            "menor-preco", "preco ASC, id",
            "maior-preco", "preco DESC, id",
            "pontuacao", PONTOS + " DESC NULLS LAST, id",
            "novidades", "id DESC");

    /**
     * Uma página do catálogo com busca, filtros e ordenação, tudo feito pelo banco.
     * Só entram vinhos ativos.
     */
    public Pagina<Produto> buscarCatalogo(FiltroCatalogo f) {
        List<Object> parametros = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE ativo");

        // Cada palavra da busca tem de aparecer no nome, tipo, origem ou descrição
        List<String> palavras = f.getPalavras();
        String textoBusca = semAcento("concat_ws(' ', nome, tipo, origem, descricao)");
        for (String palavra : palavras) {
            where.append(" AND ").append(textoBusca).append(" LIKE ?");
            parametros.add("%" + Texto.paraBusca(palavra) + "%");
        }
        if (!f.getTipo().isEmpty()) {
            where.append(" AND ").append(SQL_TIPO.get(f.getTipo()));
        }
        if (!f.getPais().isEmpty()) {
            where.append(" AND lower(").append(PAIS).append(") = lower(?)");
            parametros.add(f.getPais());
        }
        if (f.getPrecoMin() != null) {
            where.append(" AND preco >= ?");
            parametros.add(f.getPrecoMin());
        }
        if (f.getPrecoMax() != null) {
            where.append(" AND preco <= ?");
            parametros.add(f.getPrecoMax());
        }
        if (f.isSoDisponiveis()) {
            where.append(" AND estoque > 0");
        }

        // Na "relevância" com busca, quem tem a 1.ª palavra no NOME vem antes
        StringBuilder ordem = new StringBuilder(" ORDER BY (estoque <= 0)");
        List<Object> parametrosOrdem = new ArrayList<>();
        if ("relevancia".equals(f.getOrdem()) && !palavras.isEmpty()) {
            ordem.append(", (").append(semAcento("nome")).append(" LIKE ?) DESC");
            parametrosOrdem.add("%" + Texto.paraBusca(palavras.get(0)) + "%");
        }
        ordem.append(", ").append(SQL_ORDEM.get(f.getOrdem()));

        try (Connection conn = ConnectionFactory.getConnection()) {
            int total;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM produtos" + where)) {
                preencherParametros(stmt, parametros, 1);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    total = rs.getInt(1);
                }
            }

            // Página pedida além da última (ex.: link antigo) → mostra a última
            int totalPaginas = Math.max(1, (total + FiltroCatalogo.POR_PAGINA - 1) / FiltroCatalogo.POR_PAGINA);
            int pagina = Math.min(f.getPagina(), totalPaginas);

            List<Produto> itens = new ArrayList<>();
            String sql = "SELECT " + COLUNAS + " FROM produtos" + where + ordem + " LIMIT ? OFFSET ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int i = preencherParametros(stmt, parametros, 1);
                i = preencherParametros(stmt, parametrosOrdem, i);
                stmt.setInt(i++, FiltroCatalogo.POR_PAGINA);
                stmt.setInt(i, (pagina - 1) * FiltroCatalogo.POR_PAGINA);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        itens.add(mapear(rs));
                    }
                }
            }
            return new Pagina<>(itens, total, pagina, FiltroCatalogo.POR_PAGINA);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o catálogo", e);
        }
    }

    /** Países dos vinhos ativos, para a lista do filtro. */
    public List<String> listarPaises() {
        String sql = "SELECT DISTINCT " + PAIS + " AS pais FROM produtos WHERE ativo AND COALESCE(origem, '') <> ''"
                + " ORDER BY pais";
        List<String> paises = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                paises.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar países", e);
        }
        return paises;
    }

    /** Sugestões da lupa do cabeçalho: até 6 vinhos cujo nome, tipo ou origem contém o termo. */
    public List<Produto> sugerir(String termo, int limite) {
        String sql = "SELECT " + COLUNAS + " FROM produtos WHERE ativo AND "
                + semAcento("concat_ws(' ', nome, tipo, origem)") + " LIKE ?"
                + " ORDER BY (" + semAcento("nome") + " LIKE ?) DESC, (estoque <= 0), nome LIMIT ?";
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + Texto.paraBusca(termo) + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setInt(3, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sugestões", e);
        }
        return produtos;
    }

    private static int preencherParametros(PreparedStatement stmt, List<Object> parametros, int inicio)
            throws SQLException {
        int i = inicio;
        for (Object p : parametros) {
            stmt.setObject(i++, p);
        }
        return i;
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

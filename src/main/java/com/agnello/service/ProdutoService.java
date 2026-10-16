package com.agnello.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ProdutoDAO;
import com.agnello.model.Produto;

/**
 * Regras de leitura do catálogo.
 *
 * Os métodos de "vitrine" (destaques, relacionados) nunca derrubam a página:
 * se o banco falhar, registram o erro e devolvem uma lista vazia, porque a
 * página principal continua útil sem eles.
 */
public class ProdutoService {

    private static final Logger LOGGER = Logger.getLogger(ProdutoService.class.getName());

    /** Quantos vinhos aparecem em "Você também pode gostar". */
    public static final int MAX_RELACIONADOS = 4;

    private final ProdutoDAO produtoDAO;

    public ProdutoService() {
        this(new ProdutoDAO());
    }

    /** Construtor para testes. */
    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    /** Os 4 últimos vinhos cadastrados (lista vazia se o banco falhar). */
    public List<Produto> buscarUltimosVinhosDestaque() {
        try {
            return produtoDAO.listarUltimos4();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar os vinhos em destaque", e);
            return new ArrayList<>();
        }
    }

    /**
     * O vinho pedido, ou null se o id não existir ou estiver desativado.
     * Aqui o erro de banco NÃO é escondido: sem o produto a página não faz
     * sentido, e o Controller responde com erro 500.
     */
    public Produto buscarPorId(int id) {
        if (id <= 0) {
            return null;
        }
        Produto produto = produtoDAO.buscarPorId(id);
        // Vinho desativado no painel: para a loja, é como se não existisse
        return produto != null && produto.isAtivo() ? produto : null;
    }

    /** Outros vinhos do mesmo tipo, com estoque (lista vazia se o banco falhar). */
    public List<Produto> buscarRelacionados(Produto produto) {
        if (produto == null || produto.getTipo() == null) {
            return new ArrayList<>();
        }
        try {
            return produtoDAO.listarRelacionados(produto.getTipo(), produto.getId(), MAX_RELACIONADOS);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Erro ao buscar vinhos relacionados ao id=" + produto.getId(), e);
            return new ArrayList<>();
        }
    }
}

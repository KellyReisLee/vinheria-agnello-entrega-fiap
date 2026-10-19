package com.agnello.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.FavoritoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Produto;
import com.agnello.model.Usuario;

/** Lista de vinhos favoritos do cliente. */
public class FavoritoService {

    private static final Logger LOGGER = Logger.getLogger(FavoritoService.class.getName());

    private final FavoritoDAO dao;

    public FavoritoService() {
        this(new FavoritoDAO());
    }

    /** Construtor para testes. */
    public FavoritoService(FavoritoDAO dao) {
        this.dao = dao;
    }

    /** Ids dos favoritos (vazio se o banco falhar: os corações só aparecem vazios). */
    public Set<Integer> idsDoCliente(Usuario cliente) {
        if (cliente == null) {
            return Collections.emptySet();
        }
        try {
            return dao.listarIds(cliente.getId());
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Não foi possível carregar os favoritos", e);
            return Collections.emptySet();
        }
    }

    public List<Produto> listar(Usuario cliente) {
        return dao.listarProdutos(cliente.getId());
    }

    /** Marca ou desmarca. Devolve o estado final (true = é favorito). */
    public boolean definir(Usuario cliente, int produtoId, boolean favorito) throws RegraNegocioException {
        if (cliente == null) {
            throw new RegraNegocioException("Entre na sua conta para guardar favoritos.");
        }
        if (produtoId <= 0) {
            throw new RegraNegocioException("Vinho inválido.");
        }
        if (!favorito) {
            dao.remover(cliente.getId(), produtoId);
            return false;
        }
        if (!dao.adicionar(cliente.getId(), produtoId)) {
            throw new RegraNegocioException("Este vinho não está mais disponível.");
        }
        return true;
    }
}

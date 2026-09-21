package com.agnello.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ProdutoDAO;
import com.agnello.model.Produto;

public class ProdutoService {

    private static final Logger LOGGER = Logger.getLogger(ProdutoService.class.getName());
    private final ProdutoDAO produtoDAO;

    public ProdutoService() {
        this.produtoDAO = new ProdutoDAO();
    }

    /**
     * Retorna os 4 últimos vinhos cadastrados tratando eventuais falhas de banco de dados.
     * @return Lista de produtos (vazia em caso de erro para não quebrar a aplicação).
     */
    public List<Produto> buscarUltimosVinhosDestaque() {
        try {
            return produtoDAO.listarUltimos4();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro na camada de serviço ao buscar os últimos vinhos", e);
            // Retorna uma lista vazia para garantir resiliência
            return new ArrayList<>();
        }
    }
}
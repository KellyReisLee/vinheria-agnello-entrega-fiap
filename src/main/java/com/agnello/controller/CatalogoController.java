package com.agnello.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.model.FiltroCatalogo;
import com.agnello.model.Usuario;
import com.agnello.service.FavoritoService;
import com.agnello.service.ProdutoService;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Catálogo com busca, filtros, ordenação e paginação feitos pelo banco:
 *   /catalogo?busca=malbec&tipo=tinto&pais=Argentina&precoMin=100&precoMax=300&ordem=menor-preco&pagina=2
 *
 * Antes, o servidor mandava TODOS os vinhos e o JavaScript escondia os que não
 * combinavam. Com 2.000 vinhos a página ficaria enorme; agora o banco devolve
 * só os 12 da página pedida, e o endereço pode ser compartilhado ou guardado.
 */
@WebServlet(name = "CatalogoController", urlPatterns = {"/catalogo"})
public class CatalogoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CatalogoController.class.getName());

    private final ProdutoService produtoService = new ProdutoService();
    private final FavoritoService favoritoService = new FavoritoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FiltroCatalogo filtro = FiltroCatalogo.de(request.getParameterMap());
        request.setAttribute("filtro", filtro);
        request.setAttribute("paises", produtoService.listarPaises());

        try {
            request.setAttribute("pagina", produtoService.buscarCatalogo(filtro));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar o catálogo", e);
            request.setAttribute("errorMessage",
                    "Desculpe, não foi possível carregar o nosso catálogo no momento. Tente novamente mais tarde.");
        }

        // Corações preenchidos nos vinhos que o cliente já favoritou
        Usuario cliente = SessaoCliente.atual(request);
        request.setAttribute("favoritosIds",
                cliente == null ? Collections.emptySet() : favoritoService.idsDoCliente(cliente));

        request.getRequestDispatcher("/WEB-INF/views/catalogo.jsp").forward(request, response);
    }
}

package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.model.Produto;
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
 * Página de um vinho: /produto?id=12
 *
 * Fluxo: lê o id da URL → pede o vinho ao ProdutoService → coloca no request
 * → encaminha para o produto.jsp. Se o vinho não existir, o JSP mostra
 * "Vinho não encontrado" e o navegador recebe o código 404 (importante para o
 * Google não indexar páginas vazias).
 */
@WebServlet(name = "ProdutoController", urlPatterns = {"/produto"})
public class ProdutoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProdutoController.class.getName());

    private final ProdutoService produtoService = new ProdutoService();
    private final FavoritoService favoritoService = new FavoritoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = lerId(request.getParameter("id"));

        Produto produto;
        try {
            produto = produtoService.buscarPorId(id);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar o produto id=" + id, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (produto == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            request.setAttribute("relacionados", produtoService.buscarRelacionados(produto));
            // Coração preenchido se o cliente já guardou este vinho
            Usuario cliente = SessaoCliente.atual(request);
            request.setAttribute("favorito",
                    cliente != null && favoritoService.idsDoCliente(cliente).contains(produto.getId()));
        }
        request.setAttribute("produto", produto);
        request.getRequestDispatcher("/WEB-INF/views/produto.jsp").forward(request, response);
    }

    /** Converte o texto da URL em número; qualquer valor inválido vira -1 ("não encontrado"). */
    private static int lerId(String valor) {
        if (valor == null) {
            return -1;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

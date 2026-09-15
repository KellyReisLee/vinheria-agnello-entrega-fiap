package com.agnello.controller;

import com.agnello.dao.ProdutoDAO;
import com.agnello.model.Produto;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CatalogoController", urlPatterns = {"/catalogo"})
public class CatalogoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Logger padrão do Java para registrar erros no console/servidor de forma segura
    private static final Logger LOGGER = Logger.getLogger(CatalogoController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Instancia o DAO e busca todos os produtos do banco Neon
            ProdutoDAO produtoDAO = new ProdutoDAO();
            List<Produto> listaProdutos = produtoDAO.listarTodos();

            // Adiciona a lista de produtos como atributo da requisição
            request.setAttribute("produtos", listaProdutos);

            // Redireciona de forma segura para o JSP dentro de WEB-INF
            request.getRequestDispatcher("/WEB-INF/views/catalogo.jsp").forward(request, response);

        } catch (Exception e) {
            // 1. Registra o erro detalhado no log do servidor (para o desenvolvedor debugar)
            LOGGER.log(Level.SEVERE, "Erro crítico ao carregar o catálogo de produtos", e);

            // 2. Define uma mensagem amigável para ser exibida na tela de erro ou de volta ao catálogo
            request.setAttribute("errorMessage", "Desculpe, não foi possível carregar o nosso catálogo no momento. Tente novamente mais tarde.");

            // 3. Redireciona para uma página de erro dedicada (ou devolve para o catálogo com aviso)
            // Se tiver uma página de erro geral, pode usar: request.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(request, response);
            request.getRequestDispatcher("/WEB-INF/views/catalogo.jsp").forward(request, response);
        }
    }
}
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

@WebServlet("/sugestoes")
public class SugestaoSommelierController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Configuração do Logger para rastreabilidade profissional de erros
    private static final Logger LOGGER = Logger.getLogger(SugestaoSommelierController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Tenta buscar os dados do catálogo no banco Neon através do DAO
            ProdutoDAO produtoDAO = new ProdutoDAO();
            List<Produto> listaProdutos = produtoDAO.listarTodos();

            // Passa os dados obtidos para a camada de visualização (JSP)
            request.setAttribute("produtos", listaProdutos);

            // Encaminha com segurança para a view protegida (atualizado sem hífen para evitar ClassNotFoundException)
            request.getRequestDispatcher("/WEB-INF/views/sugestoes-sommelier.jsp").forward(request, response);

        } catch (RuntimeException e) {
            // Captura falhas encapsuladas pelo DAO (como problemas de conexão com o banco Neon, SQL inválido, etc.)
            LOGGER.log(Level.SEVERE, "Falha de infraestrutura ou banco de dados ao carregar sugestões do sommelier: " + e.getMessage(), e);
            
            request.setAttribute("errorMessage", "Não foi possível conectar ao nosso servidor de adega no momento. Por favor, tente novamente mais tarde.");
            request.getRequestDispatcher("/WEB-INF/views/sugestoes-sommelier.jsp").forward(request, response);

        } catch (Exception e) {
            // Captura qualquer outra exceção genérica e inesperada do ciclo do Servlet
            LOGGER.log(Level.SEVERE, "Erro crítico inesperado no fluxo de sugestões do sommelier", e);
            
            request.setAttribute("errorMessage", "Ocorreu um erro inesperado ao processar sua solicitação.");
            request.getRequestDispatcher("/WEB-INF/views/sugestoes-sommelier.jsp").forward(request, response);
        }
    }
}
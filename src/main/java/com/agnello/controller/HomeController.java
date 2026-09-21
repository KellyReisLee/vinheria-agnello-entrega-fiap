package com.agnello.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.agnello.model.Produto;
import com.agnello.service.ProdutoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet(name = "HomeController", urlPatterns = {"/home", ""})
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
    private final ProdutoService produtoService;

    public HomeController() {
        this.produtoService = new ProdutoService();
    }

    public HomeController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Pede os dados ao serviço
            List<Produto> listaVinhos = produtoService.buscarUltimosVinhosDestaque();
            
            // Atributos para a View
            request.setAttribute("listaVinhos", listaVinhos);
            
            // Encaminha para o JSP com segurança
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
            
        } catch (ServletException | IOException e) {
            // Erros de infraestrutura web/servlet devem ser logados e repassados
            LOGGER.log(Level.SEVERE, "Erro crítico de IO/Servlet ao renderizar a Home", e);
            throw e; // Permite que o container trate o erro HTTP adequadamente
        }
    }
}
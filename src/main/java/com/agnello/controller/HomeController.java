package com.agnello.controller;

import java.io.IOException;
import java.util.List;

import com.agnello.dao.ProdutoDAO;
import com.agnello.model.Produto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "HomeController", urlPatterns = {"/home", ""})
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Instancia o DAO e busca os 4 últimos vinhos cadastrados
        ProdutoDAO produtoDAO = new ProdutoDAO();
        List<Produto> listaVinhos = produtoDAO.listarUltimos4();
        
        // Envia a lista para a página JSP
        request.setAttribute("listaVinhos", listaVinhos);
        
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
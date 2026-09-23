package com.agnello.controller;

import com.agnello.dao.ClienteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AtivarContaController", urlPatterns = {"/ativar"})
public class AtivarContaController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        
        if (token != null && !token.trim().isEmpty()) {
            try {
                // Altera o estado na base de dados para true e limpa o token
                boolean ativado = clienteDAO.ativarContaPorToken(token);
                
                if (ativado) {
                    // Define uma mensagem de sucesso para ser exibida na página de login
                    request.setAttribute("sucesso", "Conta ativada com sucesso! Já pode efetuar o login.");
                } else {
                    request.setAttribute("erro", "Link de ativação inválido ou conta já ativada.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("erro", "Erro ao ativar a conta: " + e.getMessage());
            }
        } else {
            request.setAttribute("erro", "Token de ativação ausente.");
        }
        
        // Redireciona (forward) para a página de login para o cliente entrar na conta
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}
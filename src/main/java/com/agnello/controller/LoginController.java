package com.agnello.controller;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Trata a requisição GET ao acessar a página de login pelo link do menu
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            ClienteDAO dao = new ClienteDAO();
            Usuario usuario = dao.buscarPorEmail(email);

            // Valida se o usuário existe e se a senha confere
            if (usuario != null && usuario.getSenha().equals(senha)) {
                // Cria uma sessão HTTP para manter o usuário logado
                HttpSession session = request.getSession();
                session.setAttribute("clienteLogado", usuario); // Padronizado como clienteLogado para alinhar com a área pessoal

                // Redireciona diretamente para a área pessoal protegida
                response.sendRedirect(request.getContextPath() + "/area-pessoa");
            } else {
                // Caso falhe, retorna para a tela de login com erro
                request.setAttribute("erro", "E-mail ou senha inválidos.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("erro", "Erro interno no sistema: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
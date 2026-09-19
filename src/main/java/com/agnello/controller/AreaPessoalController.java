package com.agnello.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.agnello.model.Usuario;

@WebServlet(name = "AreaPessoalController", urlPatterns = {"/area-pessoal"})
public class AreaPessoalController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Evita cache da página protegida
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        HttpSession session = request.getSession(false);
        Usuario usuario = null;
        
        if (session != null) {
            // Suporta ambas as chaves de sessão para total compatibilidade
            usuario = (Usuario) session.getAttribute("clienteLogado");
            if (usuario == null) {
                usuario = (Usuario) session.getAttribute("usuarioLogado");
            }
        }
        
        // Se o usuário não estiver autenticado, manda para o login
        if (usuario == null || usuario.getId() <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Tudo certo: exibe a página protegida com URL limpa e segura
        request.getRequestDispatcher("/WEB-INF/views/area-pessoal.jsp").forward(request, response);
    }
}
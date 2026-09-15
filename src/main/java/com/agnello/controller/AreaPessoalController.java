package com.agnello.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.agnello.model.Usuario;

@WebServlet(name = "AreaPessoaController", urlPatterns = {"/area-pessoa"})
public class AreaPessoalController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("clienteLogado") : null;
        
        // 1. Se o usuário não estiver logado na sessão, redireciona para o login
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // 2. Pega o ID enviado via parâmetro na URL (ex: /area-pessoa?id=1)
        String idParam = request.getParameter("id");
        
        // 3. Validação de segurança: se o ID não foi informado ou não pertence ao usuário logado, bloqueia
        if (idParam == null || !idParam.equals(String.valueOf(usuario.getId()))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Caminho exato apontando para a pasta views dentro de WEB-INF
        request.getRequestDispatcher("/WEB-INF/views/area-pessoal.jsp").forward(request, response);
    }
}
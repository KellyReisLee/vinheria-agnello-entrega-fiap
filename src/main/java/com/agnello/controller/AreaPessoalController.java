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
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("clienteLogado") : null;
        
        // 1. Se o usuário não estiver logado na sessão, redireciona para o login de forma segura
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // 2. Pega o ID enviado via parâmetro na URL
        String idParam = request.getParameter("id");
        String idUsuarioLogado = String.valueOf(usuario.getId());
        
        // 3. Se o ID não foi informado na URL ou é diferente do usuário logado, 
        // corrigimos redirecionando para a rota correta com o ID correto.
        if (idParam == null || !idParam.equals(idUsuarioLogado)) {
            response.sendRedirect(request.getContextPath() + "/area-pessoal?id=" + idUsuarioLogado);
            return;
        }
        
        // 4. Tudo certo: exibe a página JSP protegida dentro de WEB-INF
        request.getRequestDispatcher("/WEB-INF/views/area-pessoal.jsp").forward(request, response);
    }
}
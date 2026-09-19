package com.agnello.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.agnello.service.ClienteService;

@WebServlet("/redefinir-senha")
public class RedefinirSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Token de recuperação inválido ou ausente.");
            request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String token = request.getParameter("token");
        String novaSenha = request.getParameter("novaSenha");
        String confirmaSenha = request.getParameter("confirmaSenha");

        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Sessão de recuperação inválida. Solicite um novo link.");
            request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
            return;
        }

        try {
            HttpSession session = request.getSession();
            String emailDoUsuario = (String) session.getAttribute("token_" + token);

            if (emailDoUsuario == null && token.contains("@")) {
                emailDoUsuario = token.trim();
            }

            // Delega as regras de validação de senha e alteração ao Service
            clienteService.redefinirSenha(emailDoUsuario, novaSenha, confirmaSenha);

            // Remove o token da sessão após o uso
            session.removeAttribute("token_" + token);

            request.setAttribute("mensagemSucesso", "Sua senha foi atualizada com sucesso.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("mensagemErro", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("mensagemErro", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
        }
    }
}
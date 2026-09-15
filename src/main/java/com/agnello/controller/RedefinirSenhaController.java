package com.agnello.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.agnello.dao.ClienteDAO;

@WebServlet("/redefinir-senha")
public class RedefinirSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String token = request.getParameter("token");
            
            if (token == null || token.trim().isEmpty()) {
                request.setAttribute("mensagemErro", "Token de recuperação inválido ou ausente.");
                request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
                return;
            }

            // Encaminha para a tela de redefinição mantendo o token na URL/formulário
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("mensagemErro", "Ocorreu um erro inesperado ao carregar a página.");
            request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
        }
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

        if (novaSenha == null || novaSenha.trim().isEmpty() || confirmaSenha == null || confirmaSenha.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Por favor, preencha todos os campos de senha.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
            return;
        }

        if (!novaSenha.equals(confirmaSenha)) {
            request.setAttribute("mensagemErro", "As senhas digitadas não coincidem. Tente novamente.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
            return;
        }

        if (novaSenha.length() < 6) {
            request.setAttribute("mensagemErro", "A nova senha deve conter pelo menos 6 caracteres.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
            return;
        }

        try {
            HttpSession session = request.getSession();
            
            // Tenta recuperar o e-mail armazenado na sessão através do token UUID
            String emailDoUsuario = (String) session.getAttribute("token_" + token);

            // Fallback: se por acaso o token enviado for o próprio e-mail (para testes diretos)
            if (emailDoUsuario == null && token.contains("@")) {
                emailDoUsuario = token.trim();
            }

            if (emailDoUsuario == null || emailDoUsuario.trim().isEmpty()) {
                request.setAttribute("mensagemErro", "O link de recuperação expirou ou é inválido. Solicite um novo.");
                request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
                return;
            }

            ClienteDAO dao = new ClienteDAO();
            
            // Executa a atualização no banco de dados via DAO
            dao.atualizarSenhaPorEmail(emailDoUsuario, novaSenha);

            // Remove o token da sessão após o uso (limpeza)
            session.removeAttribute("token_" + token);

            // Define a mensagem de sucesso e envia via forward para a JSP renderizar o aviso na tela
            request.setAttribute("mensagemSucesso", "Sua senha foi atualizada com sucesso.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("mensagemErro", "Erro ao atualizar a senha no banco de dados. Tente novamente mais tarde.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
        }
    }
}
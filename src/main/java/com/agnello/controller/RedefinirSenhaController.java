package com.agnello.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
            return;
        }

        // BLINDAGEM: Valida se o token existe e ainda está dentro dos 10 minutos na base de dados
        boolean tokenValido = clienteService.validarTokenRecuperacao(token);

        if (!tokenValido) {
            request.setAttribute("mensagemErro", "Este link de recuperação expirou (limite de 10 minutos) ou já foi utilizado. Por favor, solicite um novo.");
            request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
            return;
        }

        // Se estiver válido, envia o token para o JSP e abre a página de redefinição
        request.setAttribute("token", token);
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
            request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
            return;
        }

        try {
            // Delega a redefinição utilizando diretamente o token (valida, atualiza e invalida o token no banco)
            clienteService.redefinirSenhaComToken(token, novaSenha, confirmaSenha);

            request.setAttribute("mensagemSucesso", "Sua senha foi atualizada com sucesso.");
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            // Erros de preenchimento (ex: senhas não coincidem, menos de 6 carateres)
            // Devolve o token para o request para manter o formulário funcional
            request.setAttribute("token", token);
            request.setAttribute("mensagemErro", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/redefinir-senha.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Erros de token expirado, inválido ou já utilizado
            request.setAttribute("mensagemErro", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
        }
    }
}
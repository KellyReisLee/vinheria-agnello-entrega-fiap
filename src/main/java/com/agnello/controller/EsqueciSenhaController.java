package com.agnello.controller;

import com.agnello.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/esqueci-senha")
public class EsqueciSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(EsqueciSenhaController.class.getName());
    
    private ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");

        try {
            // Descobre a URL base para montar o link dinamicamente
            String baseUrl = request.getRequestURL().toString().replace("esqueci-senha", "redefinir-senha");
            
            // O service valida o e-mail e retorna o link formatado (ou null se o e-mail não existir)
            String linkRedefinicao = clienteService.solicitarRecuperacaoSenha(email, baseUrl);

            // SEGURANÇA E FLUXO: Se o link for gerado (e-mail existe), tratamos o token
            if (linkRedefinicao != null) {
                int indiceToken = linkRedefinicao.indexOf("?token=");
                if (indiceToken != -1) {
                    String token = linkRedefinicao.substring(indiceToken + 7);
                    request.getSession().setAttribute("token_" + token, email.trim());
                }
            }

            // MENSAGEM NEUTRA: Exatamente a mesma resposta quer o e-mail exista ou não 
            String mensagemSucesso = "Se o e-mail informado estiver registado, as instruções de recuperação serão enviadas.";
            request.setAttribute("mensagemSucesso", mensagemSucesso);

        } catch (IllegalArgumentException e) {
            // Erros de validação de negócio (ex: campo vazio)
            LOGGER.log(Level.WARNING, "Tentativa inválida de recuperação de senha. Motivo: {0}", e.getMessage());
            request.setAttribute("mensagemErro", e.getMessage());
            
        } catch (Exception e) {
            // Qualquer outro erro (incluindo falhas de base de dados que subiram como RuntimeException)
            LOGGER.log(Level.SEVERE, "Erro crítico ou inesperado no fluxo de esqueci-senha para o e-mail: " + email, e);
            request.setAttribute("mensagemErro", "Ocorreu um erro interno ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        }

        request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
    }
}
package com.agnello.controller;

import com.agnello.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException; // Importante para detetar erros de BD
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/esqueci-senha")
public class EsqueciSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    // Criação do Logger oficial para esta classe
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
            
            // O service valida o e-mail e retorna o link formatado
            String linkRedefinicao = clienteService.solicitarRecuperacaoSenha(email, baseUrl);

            // Validação defensiva do token
            int indiceToken = linkRedefinicao.indexOf("?token=");
            if (indiceToken != -1) {
                String token = linkRedefinicao.substring(indiceToken + 7);
                request.getSession().setAttribute("token_" + token, email.trim());
            } else {
                throw new IllegalStateException("Erro estrutural ao gerar o link de redefinição.");
            }

            String mensagemHtml = "<a href='" + linkRedefinicao + "'>Clique aqui para redefinir a senha</a>";
            request.setAttribute("mensagemSucesso", mensagemHtml);

        } catch (IllegalArgumentException e) {
            // Erros de validação de negócio (ex: e-mail inválido ou não encontrado)
            // Usamos nível WARNING para registar tentativas inválidas sem alarmar o sistema
            LOGGER.log(Level.WARNING, "Tentativa inválida de recuperação de senha para o e-mail: {0}. Motivo: {1}", new Object[]{email, e.getMessage()});
            request.setAttribute("mensagemErro", e.getMessage());
            
        } catch (SQLException | java.net.ConnectException e) {
            // Tratamento específico para falhas de conexão com o Banco de Dados ou infraestrutura
            LOGGER.log(Level.SEVERE, "Erro crítico de conexão com a base de dados ao processar recuperação para: " + email, e);
            request.setAttribute("mensagemErro", "O sistema de dados está temporariamente indisponível. Por favor, tente novamente mais tarde.");
            
        } catch (Exception e) {
            // Qualquer outro erro inesperado no sistema
            LOGGER.log(Level.SEVERE, "Erro inesperado no fluxo de esqueci-senha para o e-mail: " + email, e);
            request.setAttribute("mensagemErro", "Ocorreu um erro interno ao processar sua solicitação. Por favor, tente novamente.");
        }

        request.getRequestDispatcher("/WEB-INF/views/esqueciSenha.jsp").forward(request, response);
    }
}
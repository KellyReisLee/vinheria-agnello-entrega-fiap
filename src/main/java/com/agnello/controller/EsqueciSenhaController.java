package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.service.ClienteService;
import com.agnello.util.UrlAplicacao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/esqueci-senha")
public class EsqueciSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EsqueciSenhaController.class.getName());
    private static final String PAGINA = "/WEB-INF/views/esqueciSenha.jsp";

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            clienteService.solicitarRecuperacaoSenha(request.getParameter("email"), UrlAplicacao.base(request));
            // A mesma mensagem quer o e-mail exista ou não (não revela quem é cliente)
            request.setAttribute("mensagemSucesso",
                    "Se o e-mail estiver cadastrado, você vai receber um link para criar uma nova senha.");

        } catch (RegraNegocioException e) {
            request.setAttribute("mensagemErro", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao pedir recuperação de senha", e);
            request.setAttribute("mensagemErro", "Não foi possível processar o pedido agora. Tente novamente em alguns instantes.");
        }

        request.getRequestDispatcher(PAGINA).forward(request, response);
    }
}

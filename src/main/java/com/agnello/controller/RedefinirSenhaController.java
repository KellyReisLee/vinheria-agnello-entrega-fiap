package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.service.ClienteService;
import com.agnello.service.ClienteService.TokenInvalidoException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/redefinir-senha")
public class RedefinirSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RedefinirSenhaController.class.getName());
    private static final String PAGINA = "/WEB-INF/views/redefinir-senha.jsp";
    private static final String PAGINA_PEDIR_NOVO = "/WEB-INF/views/esqueciSenha.jsp";
    private static final String MSG_LINK_INVALIDO = "Este link de recuperação expirou ou já foi usado. Peça um novo.";

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // A página tem um token secreto no endereço: não deve ficar em cache nem ser
        // enviada a outros sites no cabeçalho Referer
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Referrer-Policy", "no-referrer");

        String token = request.getParameter("token");
        if (!clienteService.validarTokenRecuperacao(token)) {
            request.setAttribute("mensagemErro", MSG_LINK_INVALIDO);
            request.getRequestDispatcher(PAGINA_PEDIR_NOVO).forward(request, response);
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        String token = request.getParameter("token");

        try {
            clienteService.redefinirSenhaComToken(token,
                    request.getParameter("novaSenha"), request.getParameter("confirmaSenha"));
            request.setAttribute("mensagemSucesso", "Sua senha foi atualizada com sucesso.");
            request.getRequestDispatcher(PAGINA).forward(request, response);

        } catch (TokenInvalidoException e) {
            request.setAttribute("mensagemErro", MSG_LINK_INVALIDO);
            request.getRequestDispatcher(PAGINA_PEDIR_NOVO).forward(request, response);

        } catch (RegraNegocioException e) {
            // Erro de preenchimento: devolve o formulário com o mesmo token
            request.setAttribute("token", token);
            request.setAttribute("mensagemErro", e.getMessage());
            request.getRequestDispatcher(PAGINA).forward(request, response);

        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao redefinir senha", e);
            request.setAttribute("token", token);
            request.setAttribute("mensagemErro", "Não foi possível atualizar a senha agora. Tente novamente em alguns instantes.");
            request.getRequestDispatcher(PAGINA).forward(request, response);
        }
    }
}

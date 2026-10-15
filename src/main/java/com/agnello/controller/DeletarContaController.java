package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Usuario;
import com.agnello.service.ClienteService;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/deletar-conta")
public class DeletarContaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DeletarContaController.class.getName());

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        // O usuário vem SEMPRE da sessão; o formulário só envia a confirmação do e-mail
        Usuario usuario = SessaoCliente.atual(request);
        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Sessão expirada. Entre novamente.");
            return;
        }

        try {
            clienteService.deletarConta(usuario, request.getParameter("emailConfirmacao"));
            LOGGER.log(Level.INFO, "Conta excluída a pedido do cliente id={0}", usuario.getId());
            SessaoCliente.encerrar(request);
            response.getWriter().write("Conta encerrada com sucesso.");

        } catch (RegraNegocioException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());

        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir a conta do cliente id=" + usuario.getId(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Não foi possível excluir a conta agora. Tente novamente em alguns instantes.");
        }
    }
}

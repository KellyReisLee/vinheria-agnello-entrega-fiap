package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.service.ClienteService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AtivarContaController", urlPatterns = {"/ativar"})
public class AtivarContaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AtivarContaController.class.getName());

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (clienteService.ativarConta(request.getParameter("token"))) {
                request.setAttribute("sucesso", "Conta ativada com sucesso! Já pode entrar.");
            } else {
                request.setAttribute("erro", "Este link de ativação é inválido, expirou ou a conta já foi ativada.");
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao ativar conta", e);
            request.setAttribute("erro", "Não foi possível ativar a conta agora. Tente novamente em alguns instantes.");
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}

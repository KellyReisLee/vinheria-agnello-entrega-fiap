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

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private static final String PAGINA = "/WEB-INF/views/login.jsp";

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Quem já está logado vai direto para a área pessoal
        if (SessaoCliente.atual(request) != null) {
            response.sendRedirect(request.getContextPath() + "/area-pessoal");
            return;
        }
        request.getRequestDispatcher(PAGINA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");

        try {
            Usuario usuario = clienteService.autenticar(email, request.getParameter("senha"));
            SessaoCliente.iniciar(request, usuario);
            response.sendRedirect(request.getContextPath() + "/area-pessoal");
            return;

        } catch (RegraNegocioException e) {
            request.setAttribute("erro", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no login", e);
            request.setAttribute("erro", "Não foi possível entrar agora. Tente novamente em alguns instantes.");
        }

        request.getRequestDispatcher(PAGINA).forward(request, response);
    }
}

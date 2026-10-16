package com.agnello.controller.admin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Administrador;
import com.agnello.service.AdministradorService;
import com.agnello.util.IpCliente;
import com.agnello.util.LimiteTentativas;
import com.agnello.util.SessaoAdmin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Entrada do painel: GET mostra o formulário, POST confere e-mail e senha. */
@WebServlet(name = "AdminLoginController", urlPatterns = {"/admin/login"})
public class AdminLoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminLoginController.class.getName());
    private static final String VIEW = "/WEB-INF/views/admin/login.jsp";
    /** Além do bloqueio por conta, no máximo 10 tentativas por IP a cada 15 minutos. */
    private static final LimiteTentativas LIMITE_IP = new LimiteTentativas(10, 15 * 60 * 1000L);

    private final AdministradorService service = new AdministradorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessaoAdmin.atual(request) != null) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");

        if (!LIMITE_IP.permitir(IpCliente.de(request))) {
            mostrarErro(request, response, email, "Muitas tentativas a partir desta rede. Aguarde alguns minutos.");
            return;
        }

        try {
            Administrador admin = service.autenticar(email, request.getParameter("senha"));
            SessaoAdmin.iniciar(request, admin);
            response.sendRedirect(request.getContextPath() + "/admin");
        } catch (RegraNegocioException e) {
            mostrarErro(request, response, email, e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro no login do painel", e);
            mostrarErro(request, response, email, "Não foi possível entrar agora. Tente novamente.");
        }
    }

    private void mostrarErro(HttpServletRequest request, HttpServletResponse response, String email, String mensagem)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        request.setAttribute("erro", mensagem);
        request.setAttribute("emailDigitado", email);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}

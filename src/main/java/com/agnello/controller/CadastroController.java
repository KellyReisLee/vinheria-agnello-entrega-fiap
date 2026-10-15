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

@WebServlet(name = "CadastroController", urlPatterns = {"/cadastro"})
public class CadastroController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CadastroController.class.getName());
    private static final String PAGINA = "/WEB-INF/views/cadastro.jsp";

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
            clienteService.cadastrarCliente(
                    request.getParameter("tipo_cliente"),
                    request.getParameter("email"),
                    request.getParameter("senha"),
                    request.getParameter("telefone"),
                    request.getParameter("nome"),
                    request.getParameter("sobrenome"),
                    request.getParameter("cpf"),
                    request.getParameter("razao_social"),
                    request.getParameter("cnpj"),
                    UrlAplicacao.base(request));

            request.setAttribute("sucesso", "Cadastro realizado! Enviamos um link de ativação para o seu e-mail.");

        } catch (RegraNegocioException e) {
            request.setAttribute("erro", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no cadastro", e);
            request.setAttribute("erro", "Não foi possível concluir o cadastro agora. Tente novamente em alguns instantes.");
        }

        request.getRequestDispatcher(PAGINA).forward(request, response);
    }
}

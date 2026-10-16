package com.agnello.controller.admin;

import java.io.IOException;

import com.agnello.util.SessaoAdmin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sair do painel. É POST (com token CSRF, conferido pelo FiltroAdmin) e não
 * GET: assim um link ou imagem em outro site não consegue deslogar ninguém.
 */
@WebServlet(name = "AdminSairController", urlPatterns = {"/admin/sair"})
public class AdminSairController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessaoAdmin.encerrar(request);
        response.sendRedirect(request.getContextPath() + "/admin/login?saiu");
    }
}

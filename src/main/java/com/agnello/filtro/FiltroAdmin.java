package com.agnello.filtro;

import java.io.IOException;
import java.util.logging.Logger;

import com.agnello.model.Administrador;
import com.agnello.util.MensagemFlash;
import com.agnello.util.SessaoAdmin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Porteiro do painel: roda ANTES de qualquer página /admin/*.
 *
 * Um filtro evita repetir a mesma verificação em cada Controller (e o risco
 * de esquecer em algum). Ele:
 *  1. manda para /admin/login quem não está logado como administrador;
 *  2. recusa POST sem o token CSRF certo;
 *  3. impede o navegador de guardar as páginas em cache e de exibi-las
 *     dentro de outro site (proteção contra "clickjacking");
 *  4. disponibiliza ${csrf} e ${flash} (mensagem após salvar) para os JSPs do painel.
 */
@WebFilter("/admin/*")
public class FiltroAdmin extends HttpFilter {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FiltroAdmin.class.getName());

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");

        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        boolean paginaLogin = caminho.equals("/admin/login");

        if ("POST".equalsIgnoreCase(request.getMethod()) && !SessaoAdmin.csrfValido(request)) {
            // Acontece também quando a sessão expirou com a página aberta: volta com um aviso
            LOGGER.warning("POST no painel recusado (token CSRF ausente ou errado): " + caminho);
            MensagemFlash.erro(request, "O formulário expirou. Confira os dados e tente novamente.");
            boolean logado = SessaoAdmin.atual(request) != null;
            response.sendRedirect(request.getContextPath() + (logado ? "/admin" : "/admin/login"));
            return;
        }

        Administrador admin = SessaoAdmin.atual(request);
        if (admin == null && !paginaLogin) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        request.setAttribute("csrf", SessaoAdmin.tokenCsrf(request));
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            MensagemFlash.transferir(request);
        }
        chain.doFilter(request, response);
    }
}

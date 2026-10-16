package com.agnello.util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

import com.agnello.model.Administrador;
import com.agnello.seguranca.TokenSeguro;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Sessão do painel /admin. Usa um atributo diferente do cliente
 * ("adminLogado"): estar logado como cliente não dá acesso ao painel.
 *
 * Também guarda o token CSRF dos formulários do painel. Cada formulário
 * leva o token num campo escondido; o FiltroAdmin recusa qualquer POST
 * sem ele. Um site malicioso não consegue ler o token, por isso não
 * consegue forjar um formulário válido.
 */
public final class SessaoAdmin {

    public static final String ATRIBUTO = "adminLogado";
    public static final String ATRIBUTO_CSRF = "adminCsrf";
    public static final String CAMPO_CSRF = "csrf";
    /** O painel expira mais cedo que a loja: 20 minutos sem uso. */
    private static final int MINUTOS_INATIVIDADE = 20;

    private SessaoAdmin() {
    }

    public static void iniciar(HttpServletRequest request, Administrador admin) {
        HttpSession sessao = request.getSession(true);
        request.changeSessionId(); // evita "session fixation"
        sessao.setMaxInactiveInterval(MINUTOS_INATIVIDADE * 60);
        sessao.setAttribute(ATRIBUTO, admin);
        sessao.setAttribute(ATRIBUTO_CSRF, TokenSeguro.gerar()); // token novo a cada login
    }

    public static Administrador atual(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        return sessao == null ? null : (Administrador) sessao.getAttribute(ATRIBUTO);
    }

    public static void encerrar(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.removeAttribute(ATRIBUTO);
            sessao.removeAttribute(ATRIBUTO_CSRF);
        }
    }

    /** Token CSRF da sessão (cria um se ainda não existir, ex.: na página de login). */
    public static String tokenCsrf(HttpServletRequest request) {
        HttpSession sessao = request.getSession(true);
        String token = (String) sessao.getAttribute(ATRIBUTO_CSRF);
        if (token == null) {
            token = TokenSeguro.gerar();
            sessao.setAttribute(ATRIBUTO_CSRF, token);
        }
        return token;
    }

    /** Compara o token enviado com o da sessão em tempo constante. */
    public static boolean csrfValido(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        String esperado = sessao == null ? null : (String) sessao.getAttribute(ATRIBUTO_CSRF);
        String recebido = request.getParameter(CAMPO_CSRF);
        if (esperado == null || recebido == null) {
            return false;
        }
        return MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8),
                recebido.getBytes(StandardCharsets.UTF_8));
    }
}

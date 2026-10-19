package com.agnello.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.agnello.model.Usuario;
import com.agnello.seguranca.TokenSeguro;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Tudo o que envolve guardar e ler o cliente da sessão fica aqui. */
public final class SessaoCliente {

    public static final String ATRIBUTO = "clienteLogado";
    private static final String ATRIBUTO_CSRF = "clienteCsrf";
    /** 30 minutos sem atividade e a sessão expira. */
    private static final int MINUTOS_INATIVIDADE = 30;

    private SessaoCliente() {
    }

    /**
     * Inicia a sessão depois do login/cadastro. Troca o ID da sessão para evitar
     * "session fixation" (alguém que conhecesse o ID antigo ficaria logado junto).
     */
    public static void iniciar(HttpServletRequest request, Usuario usuario) {
        HttpSession sessao = request.getSession(true);
        request.changeSessionId();
        sessao.setMaxInactiveInterval(MINUTOS_INATIVIDADE * 60);
        sessao.setAttribute(ATRIBUTO, usuario);
        sessao.setAttribute(ATRIBUTO_CSRF, TokenSeguro.gerar());
    }

    /** Troca o cliente guardado na sessão (ex.: depois de editar os dados). */
    public static void atualizar(HttpServletRequest request, Usuario usuario) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.setAttribute(ATRIBUTO, usuario);
        }
    }

    /**
     * Token CSRF dos formulários da área pessoal (campo escondido "csrf").
     * Um site malicioso não consegue ler este valor, por isso não consegue
     * enviar um formulário válido em nome do cliente.
     */
    public static String tokenCsrf(HttpServletRequest request) {
        HttpSession sessao = request.getSession(true);
        String token = (String) sessao.getAttribute(ATRIBUTO_CSRF);
        if (token == null) {
            token = TokenSeguro.gerar();
            sessao.setAttribute(ATRIBUTO_CSRF, token);
        }
        return token;
    }

    public static boolean csrfValido(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        String esperado = sessao == null ? null : (String) sessao.getAttribute(ATRIBUTO_CSRF);
        String recebido = request.getParameter("csrf");
        return esperado != null && recebido != null
                && MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8), recebido.getBytes(StandardCharsets.UTF_8));
    }

    public static Usuario atual(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        return sessao == null ? null : (Usuario) sessao.getAttribute(ATRIBUTO);
    }

    public static void encerrar(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
    }
}

package com.agnello.util;

import com.agnello.model.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Tudo o que envolve guardar e ler o cliente da sessão fica aqui. */
public final class SessaoCliente {

    public static final String ATRIBUTO = "clienteLogado";
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

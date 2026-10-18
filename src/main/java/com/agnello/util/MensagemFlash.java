package com.agnello.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Mensagem que sobrevive a UM redirecionamento ("Status atualizado!").
 *
 * Depois de um POST, o painel redireciona para uma página GET (padrão
 * Post/Redirect/Get): assim, apertar F5 não reenvia o formulário. A mensagem
 * fica na sessão até a página seguinte a ler, e aí é apagada.
 */
public final class MensagemFlash {

    private static final String ATRIBUTO = "flash";
    private static final String ATRIBUTO_TIPO = "flashTipo";

    private MensagemFlash() {
    }

    public static void sucesso(HttpServletRequest request, String mensagem) {
        guardar(request, mensagem, "sucesso");
    }

    public static void erro(HttpServletRequest request, String mensagem) {
        guardar(request, mensagem, "erro");
    }

    /** Passa a mensagem guardada para o request (${flash}, ${flashTipo}) e apaga-a da sessão. */
    public static void transferir(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute(ATRIBUTO) == null) {
            return;
        }
        request.setAttribute(ATRIBUTO, sessao.getAttribute(ATRIBUTO));
        request.setAttribute(ATRIBUTO_TIPO, sessao.getAttribute(ATRIBUTO_TIPO));
        sessao.removeAttribute(ATRIBUTO);
        sessao.removeAttribute(ATRIBUTO_TIPO);
    }

    private static void guardar(HttpServletRequest request, String mensagem, String tipo) {
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute(ATRIBUTO, mensagem);
        sessao.setAttribute(ATRIBUTO_TIPO, tipo);
    }
}

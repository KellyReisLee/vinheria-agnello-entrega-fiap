package com.agnello.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Endereço público do site, usado nos links enviados por e-mail.
 *
 * Em produção vem da variável AGNELLO_URL_BASE (ex.: https://vinheria-agnello.onrender.com).
 * Montar o link a partir do pedido (cabeçalho Host) é perigoso: um atacante pode
 * pedir "Esqueci a senha" para a vítima com Host: site-do-atacante.com, e o e-mail
 * verdadeiro levaria a vítima — com o token — para o site falso.
 * Sem a variável (desenvolvimento local), usa o endereço do pedido.
 */
public final class UrlAplicacao {

    public static final String VAR_URL_BASE = "AGNELLO_URL_BASE";

    private UrlAplicacao() {
    }

    public static String base(HttpServletRequest request) {
        String configurada = System.getenv(VAR_URL_BASE);
        if (configurada != null && !configurada.isBlank()) {
            return configurada.trim().replaceAll("/+$", "");
        }
        String porta = (request.getServerPort() == 80 || request.getServerPort() == 443) ? ""
                : ":" + request.getServerPort();
        return request.getScheme() + "://" + request.getServerName() + porta + request.getContextPath();
    }
}

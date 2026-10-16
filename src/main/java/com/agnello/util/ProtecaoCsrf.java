package com.agnello.util;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Proteção contra CSRF ("Cross-Site Request Forgery").
 *
 * O ataque: um site malicioso faz o navegador da vítima, que está logada na
 * Agnello, enviar um POST para /pedidos sem ela perceber (o cookie de sessão
 * vai junto automaticamente).
 *
 * A defesa usada aqui, recomendada pela OWASP para pedidos feitos com fetch():
 *  1. exigir o cabeçalho "X-Requested-With". Um formulário HTML de outro site
 *     não consegue enviar cabeçalhos próprios, e um fetch() de outro site com
 *     esse cabeçalho é bloqueado pelo navegador (CORS), porque o nosso servidor
 *     não autoriza outras origens;
 *  2. se o navegador informar a origem (cabeçalho "Origin"), ela tem de ser o
 *     próprio site.
 */
public final class ProtecaoCsrf {

    public static final String CABECALHO = "X-Requested-With";

    private ProtecaoCsrf() {
    }

    public static boolean requisicaoValida(HttpServletRequest request) {
        if (request.getHeader(CABECALHO) == null) {
            return false;
        }
        String origem = request.getHeader("Origin");
        if (origem == null || origem.isEmpty()) {
            return true; // navegadores antigos ou mesma origem em GET; o cabeçalho acima já protege
        }
        try {
            String hostOrigem = URI.create(origem).getHost();
            return hostOrigem != null && hostOrigem.equalsIgnoreCase(request.getServerName());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

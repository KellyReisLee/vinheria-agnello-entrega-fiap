package com.agnello.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Descobre o IP de quem fez o pedido.
 *
 * No Render, o site fica atrás de um "proxy": request.getRemoteAddr() devolve
 * o IP do proxy, igual para todos os visitantes. O IP real vem no cabeçalho
 * X-Forwarded-For ("cliente, proxy1, proxy2"). Usamos o ÚLTIMO valor, que é o
 * acrescentado pelo proxy do Render; os anteriores podem ter sido inventados
 * por quem fez o pedido.
 */
public final class IpCliente {

    private IpCliente() {
    }

    public static String de(HttpServletRequest request) {
        String encaminhado = request.getHeader("X-Forwarded-For");
        if (encaminhado != null && !encaminhado.isBlank()) {
            String[] partes = encaminhado.split(",");
            return partes[partes.length - 1].trim();
        }
        return request.getRemoteAddr();
    }
}

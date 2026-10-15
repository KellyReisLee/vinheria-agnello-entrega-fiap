package com.agnello.util;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Escreve respostas JSON simples para os pedidos AJAX (ex.: checkout).
 * Escapa aspas e quebras de linha: uma mensagem com " não parte o JSON.
 */
public final class RespostaJson {

    private RespostaJson() {
    }

    /** Ex.: enviar(response, "sucesso", false, "mensagem", "Texto") → {"sucesso":false,"mensagem":"Texto"} */
    public static void enviar(HttpServletResponse response, Object... chavesEValores) throws IOException {
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i + 1 < chavesEValores.length; i += 2) {
            if (i > 0) {
                json.append(',');
            }
            json.append('"').append(escapar(String.valueOf(chavesEValores[i]))).append("\":");
            Object valor = chavesEValores[i + 1];
            if (valor instanceof Boolean || valor instanceof Number) {
                json.append(valor);
            } else if (valor == null) {
                json.append("null");
            } else {
                json.append('"').append(escapar(valor.toString())).append('"');
            }
        }
        json.append('}');
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

    static String escapar(String texto) {
        StringBuilder s = new StringBuilder(texto.length() + 8);
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"': s.append("\\\""); break;
                case '\\': s.append("\\\\"); break;
                case '\n': s.append("\\n"); break;
                case '\r': s.append("\\r"); break;
                case '\t': s.append("\\t"); break;
                case '<': s.append("\\u003c"); break;
                default:
                    if (c < 0x20) {
                        s.append(String.format("\\u%04x", (int) c));
                    } else {
                        s.append(c);
                    }
            }
        }
        return s.toString();
    }
}

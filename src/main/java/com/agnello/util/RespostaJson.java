package com.agnello.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
        Map<String, Object> objeto = new LinkedHashMap<>();
        for (int i = 0; i + 1 < chavesEValores.length; i += 2) {
            objeto.put(String.valueOf(chavesEValores[i]), chavesEValores[i + 1]);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(paraJson(objeto));
    }

    /**
     * Converte para texto JSON: textos, números, booleanos, null, listas (→ [ ])
     * e mapas (→ { }), inclusive uns dentro dos outros.
     */
    public static String paraJson(Object valor) {
        StringBuilder json = new StringBuilder();
        escrever(json, valor);
        return json.toString();
    }

    private static void escrever(StringBuilder json, Object valor) {
        if (valor == null) {
            json.append("null");
        } else if (valor instanceof Boolean || valor instanceof Integer || valor instanceof Long) {
            json.append(valor);
        } else if (valor instanceof BigDecimal) {
            json.append(((BigDecimal) valor).toPlainString());
        } else if (valor instanceof Number) {
            json.append(valor);
        } else if (valor instanceof Map) {
            json.append('{');
            boolean primeiro = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) valor).entrySet()) {
                if (!primeiro) {
                    json.append(',');
                }
                primeiro = false;
                json.append('"').append(escapar(String.valueOf(e.getKey()))).append("\":");
                escrever(json, e.getValue());
            }
            json.append('}');
        } else if (valor instanceof Collection) {
            json.append('[');
            boolean primeiro = true;
            for (Object item : (Collection<?>) valor) {
                if (!primeiro) {
                    json.append(',');
                }
                primeiro = false;
                escrever(json, item);
            }
            json.append(']');
        } else {
            json.append('"').append(escapar(valor.toString())).append('"');
        }
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

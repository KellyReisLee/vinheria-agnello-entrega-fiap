package com.agnello.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Leitor de JSON pequeno, para as respostas da API do Mercado Pago.
 *
 *   objeto → Map&lt;String,Object&gt;   lista → List&lt;Object&gt;   número → BigDecimal
 *   texto → String   true/false → Boolean   null → null
 *
 * Números viram BigDecimal (nunca double), porque são valores em dinheiro.
 * Para escrever JSON, use {@link RespostaJson#paraJson(Object)}.
 */
public final class Json {

    private final String texto;
    private int pos;

    private Json(String texto) {
        this.texto = texto;
    }

    /** @throws IllegalArgumentException se o texto não for JSON válido */
    public static Object ler(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("JSON vazio");
        }
        Json j = new Json(texto);
        j.espacos();
        Object valor = j.valor();
        j.espacos();
        if (j.pos != texto.length()) {
            throw j.erro("texto a mais depois do JSON");
        }
        return valor;
    }

    /** Lê um objeto JSON ({...}). */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> lerObjeto(String texto) {
        Object v = ler(texto);
        if (!(v instanceof Map)) {
            throw new IllegalArgumentException("Era esperado um objeto JSON");
        }
        return (Map<String, Object>) v;
    }

    // ------------------------------------------------------------ atalhos de leitura

    /** Segue um caminho de chaves: caminho(json, "payer", "email"). Null se faltar alguma. */
    @SuppressWarnings("unchecked")
    public static Object caminho(Map<String, Object> objeto, String... chaves) {
        Object atual = objeto;
        for (String chave : chaves) {
            if (!(atual instanceof Map)) {
                return null;
            }
            atual = ((Map<String, Object>) atual).get(chave);
        }
        return atual;
    }

    /** Texto do campo (números viram texto sem notação científica: 123456789). */
    public static String texto(Map<String, Object> objeto, String... chaves) {
        Object v = caminho(objeto, chaves);
        if (v == null) {
            return null;
        }
        return v instanceof BigDecimal ? ((BigDecimal) v).toPlainString() : v.toString();
    }

    public static BigDecimal numero(Map<String, Object> objeto, String... chaves) {
        Object v = caminho(objeto, chaves);
        return v instanceof BigDecimal ? (BigDecimal) v : null;
    }

    // ------------------------------------------------------------ leitura

    private Object valor() {
        if (pos >= texto.length()) {
            throw erro("fim inesperado");
        }
        char c = texto.charAt(pos);
        switch (c) {
            case '{': return objeto();
            case '[': return lista();
            case '"': return string();
            case 't': return palavra("true", Boolean.TRUE);
            case 'f': return palavra("false", Boolean.FALSE);
            case 'n': return palavra("null", null);
            default:
                if (c == '-' || (c >= '0' && c <= '9')) {
                    return numero();
                }
                throw erro("caractere inesperado '" + c + "'");
        }
    }

    private Map<String, Object> objeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        pos++; // {
        espacos();
        if (aceita('}')) {
            return mapa;
        }
        do {
            espacos();
            if (pos >= texto.length() || texto.charAt(pos) != '"') {
                throw erro("era esperado o nome de um campo");
            }
            String chave = string();
            espacos();
            exige(':');
            espacos();
            mapa.put(chave, valor());
            espacos();
        } while (aceita(','));
        exige('}');
        return mapa;
    }

    private List<Object> lista() {
        List<Object> lista = new ArrayList<>();
        pos++; // [
        espacos();
        if (aceita(']')) {
            return lista;
        }
        do {
            espacos();
            lista.add(valor());
            espacos();
        } while (aceita(','));
        exige(']');
        return lista;
    }

    private String string() {
        pos++; // "
        StringBuilder s = new StringBuilder();
        while (pos < texto.length()) {
            char c = texto.charAt(pos++);
            if (c == '"') {
                return s.toString();
            }
            if (c == '\\') {
                if (pos >= texto.length()) {
                    break;
                }
                char e = texto.charAt(pos++);
                switch (e) {
                    case '"': s.append('"'); break;
                    case '\\': s.append('\\'); break;
                    case '/': s.append('/'); break;
                    case 'b': s.append('\b'); break;
                    case 'f': s.append('\f'); break;
                    case 'n': s.append('\n'); break;
                    case 'r': s.append('\r'); break;
                    case 't': s.append('\t'); break;
                    case 'u':
                        if (pos + 4 > texto.length()) {
                            throw erro("\\u incompleto");
                        }
                        try {
                            s.append((char) Integer.parseInt(texto.substring(pos, pos + 4), 16));
                        } catch (NumberFormatException ex) {
                            throw erro("\\u inválido");
                        }
                        pos += 4;
                        break;
                    default:
                        throw erro("escape inválido \\" + e);
                }
            } else if (c < 0x20) {
                throw erro("caractere de controle dentro de texto");
            } else {
                s.append(c);
            }
        }
        throw erro("texto sem aspas de fecho");
    }

    private BigDecimal numero() {
        int inicio = pos;
        if (texto.charAt(pos) == '-') {
            pos++;
        }
        while (pos < texto.length() && "0123456789.eE+-".indexOf(texto.charAt(pos)) >= 0) {
            pos++;
        }
        try {
            return new BigDecimal(texto.substring(inicio, pos));
        } catch (NumberFormatException e) {
            throw erro("número inválido");
        }
    }

    private Object palavra(String palavra, Object valor) {
        if (!texto.startsWith(palavra, pos)) {
            throw erro("valor inválido");
        }
        pos += palavra.length();
        return valor;
    }

    private void espacos() {
        while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
            pos++;
        }
    }

    private boolean aceita(char c) {
        if (pos < texto.length() && texto.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private void exige(char c) {
        if (!aceita(c)) {
            throw erro("era esperado '" + c + "'");
        }
    }

    private IllegalArgumentException erro(String motivo) {
        return new IllegalArgumentException("JSON inválido na posição " + pos + ": " + motivo);
    }
}

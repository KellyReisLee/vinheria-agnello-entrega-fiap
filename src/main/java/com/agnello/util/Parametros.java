package com.agnello.util;

/** Conversões seguras de parâmetros da URL/formulário. */
public final class Parametros {

    private Parametros() {
    }

    /** "12" → 12; vazio, null ou "abc" → valorPadrao. */
    public static int inteiro(String valor, int valorPadrao) {
        if (valor == null || valor.isBlank()) {
            return valorPadrao;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return valorPadrao;
        }
    }
}

package com.agnello.util;

import java.text.Normalizer;

/** Pequenas transformações de texto. */
public final class Texto {

    private Texto() {
    }

    /**
     * Prepara uma palavra para a busca: sem acentos, em minúsculas e sem os
     * caracteres especiais do LIKE (% e _), que mudariam o significado da busca.
     * "Rosé" → "rose"; "50%" → "50".
     */
    public static String paraBusca(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().replace("%", "").replace("_", "").replace("\\", "").trim();
    }
}

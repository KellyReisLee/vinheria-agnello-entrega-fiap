package com.agnello.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JsonTest {

    @Test
    void leObjetosListasENumerosSemPerderCentavos() {
        Map<String, Object> j = Json.lerObjeto("{\"id\": 123456789012, \"valor\": 0.1, \"total\": 404.80,"
                + " \"ok\": true, \"nada\": null, \"lista\": [1, \"dois\", {\"tres\": 3}]}");
        assertEquals("123456789012", Json.texto(j, "id"));
        assertEquals(0, new BigDecimal("404.80").compareTo(Json.numero(j, "total")));
        assertEquals(new BigDecimal("0.1"), Json.numero(j, "valor"));
        assertEquals(Boolean.TRUE, j.get("ok"));
        assertNull(j.get("nada"));
        assertEquals(3, ((List<?>) j.get("lista")).size());
    }

    @Test
    void leEscapesEAcentos() {
        Map<String, Object> j = Json.lerObjeto("{\"nome\": \"Ros\\u00e9 \\\"Brut\\\"\\n\", \"url\": \"https:\\/\\/x\"}");
        assertEquals("Rosé \"Brut\"\n", j.get("nome"));
        assertEquals("https://x", j.get("url"));
    }

    @Test
    void caminhoAninhado() {
        Map<String, Object> j = Json.lerObjeto("{\"data\": {\"id\": \"42\"}}");
        assertEquals("42", Json.texto(j, "data", "id"));
        assertNull(Json.texto(j, "data", "x", "y"));
    }

    @Test
    void jsonInvalidoEhRecusado() {
        assertThrows(IllegalArgumentException.class, () -> Json.ler("{\"a\": }"));
        assertThrows(IllegalArgumentException.class, () -> Json.ler("{\"a\": 1} lixo"));
        assertThrows(IllegalArgumentException.class, () -> Json.ler("\"sem fim"));
        assertThrows(IllegalArgumentException.class, () -> Json.lerObjeto("[1, 2]"));
    }

    @Test
    void idaEVoltaComRespostaJson() {
        String texto = RespostaJson.paraJson(Map.of("frase", "Diga \"olá\" <script>"));
        assertTrue(texto.contains("\\u003c"));
        assertEquals("Diga \"olá\" <script>", Json.lerObjeto(texto).get("frase"));
    }
}

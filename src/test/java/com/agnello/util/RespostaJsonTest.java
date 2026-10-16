package com.agnello.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class RespostaJsonTest {

    @Test
    void escapaAspasBarrasEQuebrasDeLinha() {
        assertEquals("\"diz \\\"oi\\\"\\\\\\n\"", RespostaJson.paraJson("diz \"oi\"\\\n"));
    }

    @Test
    void escapaMenorQueParaNaoFecharScripts() {
        assertEquals("\"\\u003c/script>\"", RespostaJson.paraJson("</script>"));
    }

    @Test
    void listasMapasENumeros() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", true);
        m.put("total", new BigDecimal("10.50"));
        m.put("itens", List.of(1, "a"));
        m.put("nada", null);
        assertEquals("{\"ok\":true,\"total\":10.50,\"itens\":[1,\"a\"],\"nada\":null}", RespostaJson.paraJson(m));
    }
}

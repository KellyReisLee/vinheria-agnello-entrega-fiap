package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class FreteServiceTest {

    private static BigDecimal valor(String cep) {
        return FreteService.cotar(cep, new BigDecimal("100.00")).getValor();
    }

    @Test
    void valorDependeDaRegiaoDoCep() {
        assertEquals(new BigDecimal("25.00"), valor("01310-100")); // São Paulo (capital)
        assertEquals(new BigDecimal("35.00"), valor("13010-000")); // Campinas
        assertEquals(new BigDecimal("45.00"), valor("20040-002")); // Rio de Janeiro
        assertEquals(new BigDecimal("45.00"), valor("30130-010")); // Belo Horizonte
        assertEquals(new BigDecimal("59.00"), valor("40020-000")); // Salvador
        assertEquals(new BigDecimal("59.00"), valor("50030-000")); // Recife
        assertEquals(new BigDecimal("69.00"), valor("60060-000")); // Fortaleza
        assertEquals(new BigDecimal("59.00"), valor("70040-010")); // Brasília
        assertEquals(new BigDecimal("49.00"), valor("80010-000")); // Curitiba
        assertEquals(new BigDecimal("49.00"), valor("90010-000")); // Porto Alegre
    }

    @Test
    void freteGratisAPartirDeQuinhentosReais() {
        FreteService.Cotacao abaixo = FreteService.cotar("90010000", new BigDecimal("499.99"));
        assertFalse(abaixo.isGratis());
        assertEquals(new BigDecimal("49.00"), abaixo.getValor());
        assertEquals(new BigDecimal("0.01"), abaixo.getFaltaParaGratis());

        FreteService.Cotacao exato = FreteService.cotar("90010000", new BigDecimal("500.00"));
        assertTrue(exato.isGratis());
        assertEquals(new BigDecimal("0.00"), exato.getValor());
        assertEquals(BigDecimal.ZERO, exato.getFaltaParaGratis());
    }

    @Test
    void cepInvalidoNaoTemCotacao() {
        assertNull(FreteService.cotar(null, BigDecimal.TEN));
        assertNull(FreteService.cotar("", BigDecimal.TEN));
        assertNull(FreteService.cotar("0131-100", BigDecimal.TEN));
        assertNull(FreteService.cotar("013101000", BigDecimal.TEN));
    }

    @Test
    void semSubtotalFaltaOValorTodo() {
        assertEquals(new BigDecimal("500.00"), FreteService.cotar("01310100", null).getFaltaParaGratis());
    }
}

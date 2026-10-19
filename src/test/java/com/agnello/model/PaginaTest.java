package com.agnello.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

class PaginaTest {

    @Test
    void contaPaginasEFaixaMostrada() {
        Pagina<String> p = new Pagina<>(Collections.emptyList(), 28, 3, 12);
        assertEquals(3, p.getTotalPaginas());
        assertEquals(25, p.getPrimeiro());
        assertEquals(28, p.getUltimo());
        assertTrue(p.isTemAnterior());
        assertFalse(p.isTemProxima());
    }

    @Test
    void semResultados() {
        Pagina<String> p = new Pagina<>(Collections.emptyList(), 0, 1, 12);
        assertEquals(0, p.getPrimeiro());
        assertFalse(p.isTemAnterior());
        assertFalse(p.isTemProxima());
    }

    @Test
    void primeiraDeVarias() {
        Pagina<String> p = new Pagina<>(Collections.emptyList(), 2000, 1, 12);
        assertEquals(167, p.getTotalPaginas());
        assertEquals(1, p.getPrimeiro());
        assertEquals(12, p.getUltimo());
        assertTrue(p.isTemProxima());
    }
}

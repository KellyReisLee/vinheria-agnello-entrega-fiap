package com.agnello.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextoTest {

    @Test
    void tiraAcentosEMaiusculas() {
        assertEquals("rose", Texto.paraBusca("Rosé"));
        assertEquals("italia", Texto.paraBusca("  ITÁLIA "));
        assertEquals("sao joao", Texto.paraBusca("São João"));
    }

    @Test
    void tiraCaracteresEspeciaisDoLike() {
        assertEquals("50", Texto.paraBusca("50%"));
        assertEquals("abc", Texto.paraBusca("a_b\\c"));
    }

    @Test
    void nuloViraVazio() {
        assertEquals("", Texto.paraBusca(null));
    }
}

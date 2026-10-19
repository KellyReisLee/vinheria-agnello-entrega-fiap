package com.agnello.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void aceitaCaminhosDoProprioSite() {
        assertEquals("/catalogo?tipo=tinto", LoginController.destinoSeguro("/catalogo?tipo=tinto"));
        assertEquals("/produto?id=3", LoginController.destinoSeguro("/produto?id=3"));
    }

    @Test
    void recusaOutrosSites() {
        assertNull(LoginController.destinoSeguro("https://site-falso.com"));
        assertNull(LoginController.destinoSeguro("//site-falso.com"));
        assertNull(LoginController.destinoSeguro("/\\site-falso.com"));
        assertNull(LoginController.destinoSeguro("/catalogo\r\nSet-Cookie: x=1"));
    }

    @Test
    void recusaVazioEOProprioLogin() {
        assertNull(LoginController.destinoSeguro(null));
        assertNull(LoginController.destinoSeguro(""));
        assertNull(LoginController.destinoSeguro("/login?voltar=/x"));
    }
}

package com.agnello.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LimiteTentativasTest {

    @Test
    void bloqueiaDepoisDoMaximoELiberaQuandoAJanelaPassa() {
        LimiteTentativas limite = new LimiteTentativas(2, 1000);

        assertTrue(limite.permitir("ip-1", 0));
        assertTrue(limite.permitir("ip-1", 100));
        assertFalse(limite.permitir("ip-1", 200), "3.ª tentativa dentro da janela");
        assertTrue(limite.permitir("ip-2", 200), "outro IP tem o seu próprio limite");
        assertTrue(limite.permitir("ip-1", 1000), "a 1.ª tentativa já saiu da janela");
    }
}

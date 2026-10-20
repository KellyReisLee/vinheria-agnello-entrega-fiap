package com.agnello.pagamento;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AssinaturaWebhookTest {

    private static final String SEGREDO = "segredo-de-teste";

    private static String cabecalho(String dataId, String requestId, String ts) {
        String v1 = AssinaturaWebhook.hmac(SEGREDO, "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";");
        return "ts=" + ts + ",v1=" + v1;
    }

    @Test
    void assinaturaCertaEhAceite() {
        String h = cabecalho("123456", "bb56a2f1-6aae", "1742505638683");
        assertTrue(AssinaturaWebhook.valida(h, "bb56a2f1-6aae", "123456", SEGREDO));
    }

    @Test
    void outroPagamentoOuOutroSegredoEhRecusado() {
        String h = cabecalho("123456", "req-1", "1742505638683");
        assertFalse(AssinaturaWebhook.valida(h, "req-1", "999999", SEGREDO));
        assertFalse(AssinaturaWebhook.valida(h, "req-2", "123456", SEGREDO));
        assertFalse(AssinaturaWebhook.valida(h, "req-1", "123456", "outro-segredo"));
    }

    @Test
    void cabecalhoEmFaltaOuMalFormadoEhRecusado() {
        assertFalse(AssinaturaWebhook.valida(null, "r", "1", SEGREDO));
        assertFalse(AssinaturaWebhook.valida("lixo", "r", "1", SEGREDO));
        assertFalse(AssinaturaWebhook.valida("ts=1", "r", "1", SEGREDO));
        assertFalse(AssinaturaWebhook.valida(cabecalho("1", "r", "5"), "r", "1", null));
    }

    @Test
    void idComLetrasEhComparadoEmMinusculas() {
        String h = cabecalho("abc123", "r", "7");
        assertTrue(AssinaturaWebhook.valida(h, "r", "ABC123", SEGREDO));
    }
}

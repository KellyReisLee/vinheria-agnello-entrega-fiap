package com.agnello.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class StatusPedidoTest {

    @Test
    void fluxoDaEntrega() {
        assertEquals(List.of(StatusPedido.PAGO, StatusPedido.CANCELADO),
                StatusPedido.AGUARDANDO_PAGAMENTO.proximos(TipoEntrega.ENTREGA));
        assertEquals(List.of(StatusPedido.ENVIADO, StatusPedido.CANCELADO),
                StatusPedido.EM_SEPARACAO.proximos(TipoEntrega.ENTREGA));
        assertEquals(List.of(StatusPedido.ENTREGUE), StatusPedido.ENVIADO.proximos(TipoEntrega.ENTREGA));
    }

    @Test
    void retiradaNaoPassaPorEnviado() {
        assertEquals(List.of(StatusPedido.PRONTO_RETIRADA, StatusPedido.CANCELADO),
                StatusPedido.EM_SEPARACAO.proximos(TipoEntrega.RETIRADA));
    }

    @Test
    void entregueECanceladoSaoFinais() {
        assertTrue(StatusPedido.ENTREGUE.proximos(TipoEntrega.ENTREGA).isEmpty());
        assertTrue(StatusPedido.CANCELADO.proximos(TipoEntrega.RETIRADA).isEmpty());
        assertTrue(StatusPedido.CANCELADO.isFinal());
        assertFalse(StatusPedido.PAGO.isFinal());
    }

    @Test
    void naoDaParaCancelarDepoisDeEnviado() {
        assertFalse(StatusPedido.ENVIADO.proximos(TipoEntrega.ENTREGA).contains(StatusPedido.CANCELADO));
    }

    @Test
    void conversaoDoFormulario() {
        assertEquals(StatusPedido.PAGO, StatusPedido.doCodigo("PAGO"));
        assertNull(StatusPedido.doCodigo("APAGAR_TUDO"));
        assertNull(StatusPedido.doCodigo(null));
    }
}

package com.agnello.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.agnello.model.Pedido;
import com.agnello.model.StatusPedido;
import com.agnello.pagamento.PagamentoGateway;

class PagamentoControllerTest {

    private static Pedido pedido(StatusPedido status) {
        Pedido p = new Pedido();
        p.setStatus(status);
        return p;
    }

    private static PagamentoGateway pg(String status) {
        return new PagamentoGateway("1", status, null, "1", BigDecimal.ONE, "BRL", "pix");
    }

    @Test
    void oQueValeEOStatusDoNossoBanco() {
        assertEquals("aprovado", PagamentoController.situacao(pedido(StatusPedido.PAGO), null, "rejected"));
        assertEquals("cancelado", PagamentoController.situacao(pedido(StatusPedido.CANCELADO), null, "approved"));
    }

    @Test
    void enquantoAguardaUsaASituacaoDoMercadoPago() {
        Pedido p = pedido(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertEquals("pendente", PagamentoController.situacao(p, pg("pending"), null));
        assertEquals("recusado", PagamentoController.situacao(p, pg("rejected"), null));
        assertEquals("confirmando", PagamentoController.situacao(p, null, "approved"));
        assertEquals("nao_concluido", PagamentoController.situacao(p, null, null));
    }
}

package com.agnello.pagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.agnello.model.FormaPagamento;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
import com.agnello.model.TipoEntrega;
import com.agnello.util.Json;
import com.agnello.util.RespostaJson;

class MercadoPagoGatewayTest {

    private static Pedido pedido(FormaPagamento forma, int parcelas) {
        Pedido p = new Pedido();
        p.setId(57);
        p.setNumero("AG-000057");
        p.setFormaPagamento(forma);
        p.setParcelas(parcelas);
        p.setTipoEntrega(TipoEntrega.ENTREGA);
        p.setTotal(new BigDecimal("404.80"));
        p.getItens().add(new ItemPedido(1, "Malbec", "x.png", new BigDecimal("189.90"), 2));
        return p;
    }

    @Test
    @SuppressWarnings("unchecked")
    void preferenciaCobraExatamenteOTotalDaLoja() {
        Map<String, Object> pref = MercadoPagoGateway.montarPreferencia(pedido(FormaPagamento.CARTAO, 3),
                "ana@exemplo.com", "https://loja.onrender.com");
        // Passa pelo JSON de verdade, como vai para o Mercado Pago
        Map<String, Object> json = Json.lerObjeto(RespostaJson.paraJson(pref));

        List<Object> itens = (List<Object>) json.get("items");
        assertEquals(1, itens.size());
        Map<String, Object> item = (Map<String, Object>) itens.get(0);
        assertEquals(0, new BigDecimal("404.80").compareTo((BigDecimal) item.get("unit_price")));
        assertEquals("BRL", item.get("currency_id"));
        assertEquals("57", json.get("external_reference"));
        assertEquals("ana@exemplo.com", Json.texto(json, "payer", "email"));
        assertEquals("https://loja.onrender.com/pagamento/retorno?pedido=57", Json.texto(json, "back_urls", "success"));
        assertEquals("approved", json.get("auto_return"));
        assertTrue(Json.texto(json, "notification_url").startsWith("https://loja.onrender.com/pagamento/notificacao"));
        assertEquals("3", Json.texto(json, "payment_methods", "installments"));
    }

    @Test
    void emLocalhostNaoPedeAvisosNemRetornoAutomatico() {
        Map<String, Object> pref = MercadoPagoGateway.montarPreferencia(pedido(FormaPagamento.PIX, 1),
                "ana@exemplo.com", "http://localhost:8080/vinheria-agnello");
        assertFalse(pref.containsKey("notification_url"));
        assertFalse(pref.containsKey("auto_return"));
    }

    @Test
    void formaEscolhidaFicaTravada() {
        List<String> pix = MercadoPagoGateway.tiposExcluidos(FormaPagamento.PIX);
        assertTrue(pix.contains("credit_card") && pix.contains("ticket") && !pix.contains("bank_transfer"));
        List<String> cartao = MercadoPagoGateway.tiposExcluidos(FormaPagamento.CARTAO);
        assertTrue(cartao.contains("bank_transfer") && !cartao.contains("credit_card"));
        List<String> boleto = MercadoPagoGateway.tiposExcluidos(FormaPagamento.BOLETO);
        assertTrue(boleto.contains("credit_card") && !boleto.contains("ticket"));
    }

    @Test
    void lePagamentoDaApi() {
        PagamentoGateway pg = MercadoPagoGateway.lerPagamento(Json.lerObjeto("{\"id\": 1325977889,"
                + " \"status\": \"approved\", \"status_detail\": \"accredited\", \"external_reference\": \"57\","
                + " \"transaction_amount\": 404.8, \"currency_id\": \"BRL\", \"payment_method_id\": \"pix\"}"));
        assertEquals("1325977889", pg.getId());
        assertTrue(pg.isAprovado());
        assertEquals("57", pg.getReferenciaExterna());
        assertEquals(0, new BigDecimal("404.80").compareTo(pg.getValor()));
        assertEquals("pix", pg.getMeio());
    }

    @Test
    void semTokenNaoChamaAApi() {
        MercadoPagoGateway g = new MercadoPagoGateway(null);
        try {
            g.buscarPagamento("1");
        } catch (ErroGateway e) {
            assertTrue(e.getMessage().contains("MP_ACCESS_TOKEN"));
            return;
        }
        throw new AssertionError("deveria falhar sem token");
    }

    @Test
    void idQueNaoEhNumeroEhIgnorado() throws Exception {
        assertNull(new MercadoPagoGateway("x").buscarPagamento("../../users/me"));
    }
}

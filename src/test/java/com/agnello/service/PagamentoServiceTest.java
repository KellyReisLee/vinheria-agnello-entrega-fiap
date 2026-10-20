package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agnello.dao.PedidoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ClientePF;
import com.agnello.model.FormaPagamento;
import com.agnello.model.Pedido;
import com.agnello.model.StatusPedido;
import com.agnello.model.TipoEntrega;
import com.agnello.pagamento.ErroGateway;
import com.agnello.pagamento.GatewayPagamento;
import com.agnello.pagamento.PagamentoGateway;

/** Regras do pagamento com um "banco" e um "Mercado Pago" falsos (sem rede). */
class PagamentoServiceTest {

    private BancoFalso banco;
    private MercadoPagoFalso mp;
    private EmailFalso email;
    private PagamentoService service;
    private ClientePF ana;

    static class BancoFalso extends PedidoDAO {
        final Map<Integer, Pedido> pedidos = new HashMap<>();
        int confirmacoes;

        @Override
        public Pedido buscarParaAdmin(int id) {
            Pedido p = pedidos.get(id);
            if (p == null) {
                return null;
            }
            Pedido copia = copia(p);
            copia.setEmailCliente("ana@exemplo.com");
            copia.setNomeCliente("Ana");
            return copia;
        }

        @Override
        public List<Pedido> listarPorCliente(int clienteId) {
            List<Pedido> lista = new ArrayList<>();
            for (Pedido p : pedidos.values()) {
                if (p.getClienteId() == clienteId) {
                    lista.add(copia(p));
                }
            }
            return lista;
        }

        @Override
        public boolean confirmarPagamento(int pedidoId, String pagamentoId, String status) {
            Pedido p = pedidos.get(pedidoId);
            p.setPagamentoId(pagamentoId);
            p.setPagamentoStatus(status);
            if (p.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO) {
                p.setStatus(StatusPedido.PAGO);
                confirmacoes++;
                return true;
            }
            return false;
        }

        @Override
        public void registrarPagamento(int pedidoId, String pagamentoId, String status) {
            Pedido p = pedidos.get(pedidoId);
            p.setPagamentoId(pagamentoId);
            p.setPagamentoStatus(status);
        }

        private static Pedido copia(Pedido o) {
            Pedido p = new Pedido();
            p.setId(o.getId());
            p.setNumero(o.getNumero());
            p.setClienteId(o.getClienteId());
            p.setStatus(o.getStatus());
            p.setFormaPagamento(o.getFormaPagamento());
            p.setTipoEntrega(o.getTipoEntrega());
            p.setTotal(o.getTotal());
            p.setPagamentoId(o.getPagamentoId());
            p.setPagamentoStatus(o.getPagamentoStatus());
            return p;
        }
    }

    static class MercadoPagoFalso implements GatewayPagamento {
        final Map<String, PagamentoGateway> pagamentos = new HashMap<>();
        Pedido ultimaPagina;
        boolean foraDoAr;

        @Override
        public String criarPagina(Pedido pedido, String emailCliente, String urlBase) throws ErroGateway {
            if (foraDoAr) {
                throw new ErroGateway("fora do ar");
            }
            ultimaPagina = pedido;
            return "https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=teste-" + pedido.getId();
        }

        @Override
        public PagamentoGateway buscarPagamento(String id) throws ErroGateway {
            if (foraDoAr) {
                throw new ErroGateway("fora do ar");
            }
            return pagamentos.get(id);
        }

        void cria(String id, String status, String pedidoId, String valor) {
            pagamentos.put(id, new PagamentoGateway(id, status, null, pedidoId, new BigDecimal(valor), "BRL", "pix"));
        }
    }

    static class EmailFalso extends EmailService {
        int enviados;

        @Override
        public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
            enviados++;
        }
    }

    @BeforeEach
    void preparar() {
        banco = new BancoFalso();
        mp = new MercadoPagoFalso();
        email = new EmailFalso();
        service = new PagamentoService(banco, mp, email, true);
        ana = new ClientePF();
        ana.setId(7);
        ana.setEmail("ana@exemplo.com");
        ana.setNome("Ana");

        Pedido p = new Pedido();
        p.setId(57);
        p.setNumero("AG-000057");
        p.setClienteId(7);
        p.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        p.setFormaPagamento(FormaPagamento.PIX);
        p.setTipoEntrega(TipoEntrega.RETIRADA);
        p.setTotal(new BigDecimal("180.41"));
        banco.pedidos.put(57, p);
    }

    @Test
    void pagamentoAprovadoComValorCertoMarcaComoPagoUmaVezSo() throws Exception {
        mp.cria("111", "approved", "57", "180.41");

        PagamentoService.Resultado r = service.processar("111");
        assertTrue(r.isFicouPago());
        assertEquals(StatusPedido.PAGO, banco.pedidos.get(57).getStatus());
        assertEquals("111", banco.pedidos.get(57).getPagamentoId());
        assertEquals(1, email.enviados);

        // O Mercado Pago repete o aviso: nada muda e não sai outro e-mail
        PagamentoService.Resultado de_novo = service.processar("111");
        assertFalse(de_novo.isFicouPago());
        assertEquals(1, banco.confirmacoes);
        assertEquals(1, email.enviados);
    }

    @Test
    void valorDiferenteNaoMarcaComoPago() throws Exception {
        mp.cria("222", "approved", "57", "1.00");
        PagamentoService.Resultado r = service.processar("222");
        assertFalse(r.isFicouPago());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, banco.pedidos.get(57).getStatus());
        assertEquals("valor_divergente", banco.pedidos.get(57).getPagamentoStatus());
        assertEquals(0, email.enviados);
    }

    @Test
    void pixPendenteSoGuardaASituacao() throws Exception {
        mp.cria("333", "pending", "57", "180.41");
        service.processar("333");
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, banco.pedidos.get(57).getStatus());
        assertEquals("pending", banco.pedidos.get(57).getPagamentoStatus());
    }

    @Test
    void recusaAntigaNaoApagaUmPagamentoAprovado() throws Exception {
        mp.cria("444", "approved", "57", "180.41");
        mp.cria("400", "rejected", "57", "180.41");
        service.processar("444");
        service.processar("400"); // aviso atrasado de uma tentativa anterior, recusada
        assertEquals(StatusPedido.PAGO, banco.pedidos.get(57).getStatus());
        assertEquals("444", banco.pedidos.get(57).getPagamentoId());
        assertEquals("approved", banco.pedidos.get(57).getPagamentoStatus());
    }

    @Test
    void pagamentoDesconhecidoOuDeOutraLojaEhIgnorado() throws Exception {
        assertNull(service.processar("999").getPedido());
        mp.cria("555", "approved", "pedido-de-outra-loja", "10.00");
        assertNull(service.processar("555").getPedido());
        mp.cria("556", "approved", "9999", "10.00");
        assertNull(service.processar("556").getPedido());
        assertEquals(0, banco.confirmacoes);
    }

    @Test
    void iniciarSoFuncionaParaODonoEComPedidoAguardando() throws Exception {
        String url = service.iniciarPorId(ana, 57, "https://loja");
        assertTrue(url.startsWith("https://www.mercadopago.com.br/"));
        assertEquals(57, mp.ultimaPagina.getId());

        ClientePF outro = new ClientePF();
        outro.setId(8);
        assertThrows(RegraNegocioException.class, () -> service.iniciarPorId(outro, 57, "https://loja"));

        banco.pedidos.get(57).setStatus(StatusPedido.PAGO);
        assertThrows(RegraNegocioException.class, () -> service.iniciarPorId(ana, 57, "https://loja"));
    }

    @Test
    void desligadoNaoAbrePagamento() {
        PagamentoService desligado = new PagamentoService(banco, mp, email, false);
        assertThrows(RegraNegocioException.class, () -> desligado.iniciarPorId(ana, 57, "https://loja"));
    }

    @Test
    void retornoConfirmaPelaApiESoParaOPedidoCerto() throws Exception {
        mp.cria("777", "approved", "57", "180.41");
        PagamentoService.Resultado r = service.processarRetorno(ana, 57, "777");
        assertEquals(StatusPedido.PAGO, r.getPedido().getStatus());

        // Um payment_id de OUTRO pedido colado no endereço não serve para este
        Pedido outro = new Pedido();
        outro.setId(58);
        outro.setNumero("AG-000058");
        outro.setClienteId(7);
        outro.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        outro.setFormaPagamento(FormaPagamento.PIX);
        outro.setTipoEntrega(TipoEntrega.RETIRADA);
        outro.setTotal(new BigDecimal("50.00"));
        banco.pedidos.put(58, outro);
        PagamentoService.Resultado r2 = service.processarRetorno(ana, 58, "777");
        assertNull(r2.getPagamento());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, r2.getPedido().getStatus());
    }

    @Test
    void retornoDePedidoDeOutroClienteEhRecusado() {
        ClientePF outro = new ClientePF();
        outro.setId(8);
        assertThrows(RegraNegocioException.class, () -> service.processarRetorno(outro, 57, null));
    }

    @Test
    void mercadoPagoForaDoArNoRetornoNaoQuebraAPagina() throws Exception {
        mp.foraDoAr = true;
        PagamentoService.Resultado r = service.processarRetorno(ana, 57, "111");
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, r.getPedido().getStatus());
    }
}

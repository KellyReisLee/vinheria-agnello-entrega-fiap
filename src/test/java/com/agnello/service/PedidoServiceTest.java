package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agnello.dao.PedidoDAO;
import com.agnello.dao.ProdutoDAO;
import com.agnello.exception.EstoqueInsuficienteException;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ClientePF;
import com.agnello.model.FormaPagamento;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
import com.agnello.model.Produto;
import com.agnello.model.StatusPedido;
import com.agnello.model.TipoEntrega;

/**
 * Testes das regras de pedido SEM banco de dados: o banco e o e-mail são
 * trocados por versões falsas ("fakes") que guardam o que receberam.
 */
class PedidoServiceTest {

    /** Banco falso de produtos: devolve só os produtos cadastrados no mapa. */
    static class ProdutoDAOFalso extends ProdutoDAO {
        final Map<Integer, Produto> produtos = new HashMap<>();

        @Override
        public Map<Integer, Produto> buscarPorIds(Collection<Integer> ids) {
            Map<Integer, Produto> achados = new HashMap<>();
            for (Integer id : ids) {
                if (produtos.containsKey(id)) {
                    achados.put(id, produtos.get(id));
                }
            }
            return achados;
        }
    }

    /** Banco falso de pedidos: guarda os pedidos numa lista e gera o número. */
    static class PedidoDAOFalso extends PedidoDAO {
        final List<Pedido> gravados = new ArrayList<>();

        @Override
        public void inserir(Pedido pedido) {
            gravados.add(pedido);
            pedido.setId(gravados.size());
            pedido.setNumero(String.format("AG-%06d", gravados.size()));
        }
    }

    /** E-mail falso: só conta quantos seriam enviados (ou falha, se pedirmos). */
    static class EmailFalso extends EmailService {
        int enviados;
        boolean falhar;

        @Override
        public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
            if (falhar) {
                throw new IllegalStateException("Brevo fora do ar");
            }
            enviados++;
        }
    }

    private ProdutoDAOFalso produtos;
    private PedidoDAOFalso pedidos;
    private EmailFalso email;
    private PedidoService service;
    private ClientePF cliente;

    @BeforeEach
    void preparar() {
        produtos = new ProdutoDAOFalso();
        produtos.produtos.put(1, produto(1, "Malbec Reserva", 189.90, 5));
        produtos.produtos.put(2, produto(2, "Chablis", 249.00, 1));
        pedidos = new PedidoDAOFalso();
        email = new EmailFalso();
        service = new PedidoService(pedidos, produtos, email);

        cliente = new ClientePF();
        cliente.setId(7);
        cliente.setEmail("ana@exemplo.com");
        cliente.setNome("Ana");
        cliente.setSobrenome("Lima");
    }

    private static Produto produto(int id, String nome, double preco, int estoque) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        p.setPreco(preco);
        p.setEstoque(estoque);
        return p;
    }

    private static NovoPedido pedidoComEntrega(String pagamento) {
        NovoPedido n = new NovoPedido();
        n.setTipoEntrega("ENTREGA");
        n.setFormaPagamento(pagamento);
        n.setCep("01310-100");
        n.setLogradouro("Av. Paulista");
        n.setNumero("1000");
        n.setBairro("Bela Vista");
        n.setCidade("São Paulo - SP");
        return n;
    }

    // ------------------------------------------------------------------ valores

    @Test
    void pixTemCincoPorCentoDeDescontoSobreOsVinhos() throws Exception {
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(1, 2); // 2 × 189,90 = 379,80

        Pedido p = service.criarPedido(cliente, n);

        assertEquals(new BigDecimal("379.80"), p.getSubtotal());
        assertEquals(new BigDecimal("25.00"), p.getFrete());
        assertEquals(new BigDecimal("18.99"), p.getDesconto()); // 5% de 379,80
        assertEquals(new BigDecimal("385.81"), p.getTotal());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, p.getStatus());
        assertEquals("AG-000001", p.getNumero());
        assertEquals(1, email.enviados);
    }

    @Test
    void retiradaNaAdegaNaoCobraFreteNemPedeEndereco() throws Exception {
        NovoPedido n = new NovoPedido();
        n.setTipoEntrega("RETIRADA");
        n.setFormaPagamento("boleto");
        n.adicionarItem(2, 1);

        Pedido p = service.criarPedido(cliente, n);

        assertEquals(BigDecimal.ZERO.setScale(2), p.getFrete());
        assertEquals(new BigDecimal("249.00"), p.getTotal());
        assertNull(p.getEndereco());
        assertEquals(TipoEntrega.RETIRADA, p.getTipoEntrega());
    }

    @Test
    void oPrecoVemDoBancoENaoDoNavegador() throws Exception {
        // O navegador não manda preço nenhum: só id e quantidade
        NovoPedido n = pedidoComEntrega("boleto");
        n.adicionarItem(1, 1);

        Pedido p = service.criarPedido(cliente, n);

        ItemPedido item = p.getItens().get(0);
        assertEquals(new BigDecimal("189.90"), item.getPrecoUnitario());
        assertEquals("Malbec Reserva", item.getNomeProduto());
    }

    @Test
    void mesmoVinhoDuasVezesSomaAsQuantidades() throws Exception {
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(1, 1);
        n.adicionarItem(1, 2);

        Pedido p = service.criarPedido(cliente, n);

        assertEquals(1, p.getItens().size());
        assertEquals(3, p.getQuantidadeGarrafas());
    }

    // ------------------------------------------------------------------ cartão

    @Test
    void cartaoEhAprovadoNaSimulacaoComParcelasValidas() throws Exception {
        NovoPedido n = pedidoComEntrega("credit");
        n.adicionarItem(1, 2); // 379,80 + 25 = 404,80 → até 6x (mínimo de R$ 50)
        n.setParcelas(6);

        Pedido p = service.criarPedido(cliente, n);

        assertEquals(FormaPagamento.CARTAO, p.getFormaPagamento());
        assertEquals(StatusPedido.PAGO, p.getStatus());
        assertEquals(6, p.getParcelas());
        assertEquals(new BigDecimal("67.47"), p.getValorParcela());
    }

    @Test
    void recusaParcelasAbaixoDeCinquentaReais() {
        NovoPedido n = new NovoPedido();
        n.setTipoEntrega("RETIRADA");
        n.setFormaPagamento("credit");
        n.adicionarItem(1, 1); // 189,90 → no máximo 3x
        n.setParcelas(4);

        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.criarPedido(cliente, n));
        assertTrue(e.getMessage().contains("3x"));
        assertTrue(pedidos.gravados.isEmpty());
    }

    @Test
    void maximoDeParcelas() {
        assertEquals(1, PedidoService.maximoParcelas(new BigDecimal("49.99")));
        assertEquals(1, PedidoService.maximoParcelas(new BigDecimal("99.99")));
        assertEquals(2, PedidoService.maximoParcelas(new BigDecimal("100.00")));
        assertEquals(6, PedidoService.maximoParcelas(new BigDecimal("5000.00")));
    }

    // ------------------------------------------------------------------ recusas

    @Test
    void semSessaoNaoHaPedido() {
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(1, 1);
        assertThrows(RegraNegocioException.class, () -> service.criarPedido(null, n));
    }

    @Test
    void carrinhoVazioEhRecusado() {
        assertThrows(RegraNegocioException.class, () -> service.criarPedido(cliente, pedidoComEntrega("pix")));
    }

    @Test
    void vinhoInexistenteEhRecusado() {
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(999, 1);
        assertThrows(RegraNegocioException.class, () -> service.criarPedido(cliente, n));
    }

    @Test
    void estoqueInsuficienteEhRecusado() {
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(2, 2); // só há 1 Chablis
        EstoqueInsuficienteException e =
                assertThrows(EstoqueInsuficienteException.class, () -> service.criarPedido(cliente, n));
        assertTrue(e.getMessage().contains("Chablis"));
    }

    @Test
    void quantidadeInvalidaEhRecusada() {
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(1, 0);
        assertThrows(RegraNegocioException.class, () -> service.criarPedido(cliente, n));
    }

    @Test
    void formaDePagamentoDesconhecidaEhRecusada() {
        NovoPedido n = pedidoComEntrega("bitcoin");
        n.adicionarItem(1, 1);
        assertThrows(RegraNegocioException.class, () -> service.criarPedido(cliente, n));
    }

    @Test
    void entregaSemCepEhRecusada() {
        NovoPedido n = pedidoComEntrega("pix");
        n.setCep("123");
        n.adicionarItem(1, 1);
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.criarPedido(cliente, n));
        assertTrue(e.getMessage().contains("CEP"));
    }

    // ------------------------------------------------------------------ outros

    @Test
    void enderecoFicaNumaLinhaFormatada() throws Exception {
        NovoPedido n = pedidoComEntrega("pix");
        n.setComplemento(" apto   12 ");
        assertEquals("Av. Paulista, 1000 – apto 12 · Bela Vista · São Paulo - SP · CEP 01310-100",
                PedidoService.montarEndereco(n));
    }

    @Test
    void falhaNoEmailNaoDesfazOPedido() throws Exception {
        email.falhar = true;
        NovoPedido n = pedidoComEntrega("pix");
        n.adicionarItem(1, 1);

        Pedido p = service.criarPedido(cliente, n);

        assertNotNull(p.getNumero());
        assertEquals(1, pedidos.gravados.size());
    }
}

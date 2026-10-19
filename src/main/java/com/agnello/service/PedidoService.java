package com.agnello.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.PedidoDAO;
import com.agnello.dao.ProdutoDAO;
import com.agnello.exception.EstoqueInsuficienteException;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Administrador;
import com.agnello.model.FormaPagamento;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
import com.agnello.model.Produto;
import com.agnello.model.StatusPedido;
import com.agnello.model.TipoEntrega;
import com.agnello.model.Usuario;
import com.agnello.util.EmailTemplateUtil;
import com.agnello.util.Validacao;

/**
 * Regras de negócio dos pedidos.
 *
 * Regra de ouro do e-commerce: o servidor NUNCA confia em valores vindos do
 * navegador. Preços, frete, desconto e total são recalculados aqui, com os
 * preços do banco. O navegador só informa quais vinhos e quantas garrafas.
 *
 * As mesmas regras aparecem no checkout.js, mas lá servem apenas para mostrar
 * os valores enquanto o cliente escolhe; o valor que vale é o daqui.
 */
public class PedidoService {

    private static final Logger LOGGER = Logger.getLogger(PedidoService.class.getName());

    public static final BigDecimal VALOR_FRETE = new BigDecimal("25.00");
    /** Desconto para pagamento com Pix (5%). */
    public static final BigDecimal DESCONTO_PIX = new BigDecimal("0.05");
    public static final int MAX_PARCELAS = 6;
    /** Cada parcela do cartão tem de ser de pelo menos R$ 50. */
    public static final BigDecimal PARCELA_MINIMA = new BigDecimal("50");
    public static final int MAX_QUANTIDADE_POR_VINHO = 99;
    public static final int MAX_VINHOS_DIFERENTES = 30;

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final EmailService emailService;

    public PedidoService() {
        this(new PedidoDAO(), new ProdutoDAO(), new EmailService());
    }

    /** Construtor para testes (permite usar versões falsas do banco e do e-mail). */
    public PedidoService(PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, EmailService emailService) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.emailService = emailService;
    }

    // ===================================================================== criar

    /**
     * Valida, calcula e grava o pedido. Devolve o pedido gravado (com número).
     * Mensagens de RegraNegocioException podem ser mostradas ao cliente.
     */
    public Pedido criarPedido(Usuario cliente, NovoPedido dados) throws RegraNegocioException {
        if (cliente == null) {
            throw new RegraNegocioException("Entre na sua conta para concluir o pedido.");
        }
        if (dados == null || dados.getItens().isEmpty()) {
            throw new RegraNegocioException("O seu carrinho está vazio.");
        }
        if (dados.getItens().size() > MAX_VINHOS_DIFERENTES) {
            throw new RegraNegocioException("O pedido tem vinhos demais. Divida em mais de uma compra.");
        }

        TipoEntrega entrega = TipoEntrega.doCodigo(dados.getTipoEntrega());
        if (entrega == null) {
            throw new RegraNegocioException("Escolha o método de envio.");
        }
        FormaPagamento pagamento = FormaPagamento.doCodigo(dados.getFormaPagamento());
        if (pagamento == null) {
            throw new RegraNegocioException("Escolha a forma de pagamento.");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(cliente.getId());
        pedido.setTipoEntrega(entrega);
        pedido.setFormaPagamento(pagamento);
        pedido.setItens(montarItens(dados.getItens()));
        pedido.setEndereco(entrega == TipoEntrega.ENTREGA ? montarEndereco(dados) : null);

        calcularValores(pedido);
        pedido.setParcelas(validarParcelas(pagamento, dados.getParcelas(), pedido.getTotal()));

        // Sem um gateway de pagamento real, o cartão é dado como aprovado (simulação).
        // Num site em produção, aqui entraria a chamada ao gateway (Mercado Pago,
        // Pagar.me, Stripe…) com o "token" do cartão gerado no navegador — o número do
        // cartão nunca passa pelo nosso servidor.
        pedido.setStatus(pagamento == FormaPagamento.CARTAO ? StatusPedido.PAGO : StatusPedido.AGUARDANDO_PAGAMENTO);

        pedidoDAO.inserir(pedido);
        LOGGER.log(Level.INFO, "Pedido {0} criado para o cliente id={1}",
                new Object[] {pedido.getNumero(), cliente.getId()});

        enviarConfirmacao(cliente, pedido);
        return pedido;
    }

    /** Busca os vinhos no banco e monta os itens com o preço e o nome atuais. */
    private List<ItemPedido> montarItens(Map<Integer, Integer> solicitados) throws RegraNegocioException {
        for (Map.Entry<Integer, Integer> e : solicitados.entrySet()) {
            if (e.getKey() == null || e.getKey() <= 0 || e.getValue() == null
                    || e.getValue() < 1 || e.getValue() > MAX_QUANTIDADE_POR_VINHO) {
                throw new RegraNegocioException("Quantidade inválida no carrinho (de 1 a "
                        + MAX_QUANTIDADE_POR_VINHO + " garrafas por vinho).");
            }
        }

        Map<Integer, Produto> produtos = produtoDAO.buscarPorIds(solicitados.keySet());
        List<ItemPedido> itens = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : solicitados.entrySet()) {
            Produto produto = produtos.get(e.getKey());
            if (produto == null || !produto.isAtivo()) {
                throw new RegraNegocioException("Um dos vinhos do carrinho não está mais disponível. "
                        + "Remova-o e tente novamente.");
            }
            if (produto.getEstoque() < e.getValue()) {
                throw new EstoqueInsuficienteException(produto.getNome());
            }
            itens.add(new ItemPedido(produto.getId(), produto.getNome(), produto.getImagem(),
                    dinheiro(BigDecimal.valueOf(produto.getPreco())), e.getValue()));
        }
        return itens;
    }

    /** Subtotal, frete, desconto Pix e total. Público para poder ser testado sozinho. */
    public static void calcularValores(Pedido pedido) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItens()) {
            subtotal = subtotal.add(item.getTotal());
        }
        subtotal = dinheiro(subtotal);

        BigDecimal frete = pedido.getTipoEntrega() == TipoEntrega.ENTREGA ? VALOR_FRETE : BigDecimal.ZERO;
        BigDecimal desconto = pedido.getFormaPagamento() == FormaPagamento.PIX
                ? dinheiro(subtotal.multiply(DESCONTO_PIX))
                : BigDecimal.ZERO;

        pedido.setSubtotal(subtotal);
        pedido.setFrete(dinheiro(frete));
        pedido.setDesconto(dinheiro(desconto));
        pedido.setTotal(dinheiro(subtotal.add(frete).subtract(desconto)));
    }

    /** Até 6x sem juros, com parcela mínima de R$ 50 (a mesma regra do checkout.js). */
    public static int maximoParcelas(BigDecimal total) {
        int pelaParcelaMinima = total.divide(PARCELA_MINIMA, 0, RoundingMode.DOWN).intValue();
        return Math.max(1, Math.min(MAX_PARCELAS, pelaParcelaMinima));
    }

    private static int validarParcelas(FormaPagamento pagamento, int solicitadas, BigDecimal total)
            throws RegraNegocioException {
        if (pagamento != FormaPagamento.CARTAO) {
            return 1;
        }
        int maximo = maximoParcelas(total);
        if (solicitadas < 1 || solicitadas > maximo) {
            throw new RegraNegocioException("Para este valor, o parcelamento é de até " + maximo + "x.");
        }
        return solicitadas;
    }

    /** Valida o endereço e devolve-o numa linha: "Rua X, 12 – ap. 3 · Bairro · Cidade - UF · CEP 01234-567". */
    static String montarEndereco(NovoPedido d) throws RegraNegocioException {
        String cep = Validacao.somenteDigitos(d.getCep());
        if (cep == null || cep.length() != 8) {
            throw new RegraNegocioException("Informe um CEP com 8 dígitos.");
        }
        String logradouro = obrigatorio(d.getLogradouro(), 120, "Informe a rua.");
        String numero = obrigatorio(d.getNumero(), 20, "Informe o número.");
        String bairro = obrigatorio(d.getBairro(), 80, "Informe o bairro.");
        String cidade = obrigatorio(d.getCidade(), 80, "Informe a cidade.");
        String complemento = Validacao.vazio(d.getComplemento()) ? null : d.getComplemento().trim().replaceAll("\\s+", " ");
        if (complemento != null && complemento.length() > 60) {
            throw new RegraNegocioException("O complemento pode ter no máximo 60 caracteres.");
        }

        return logradouro + ", " + numero + (complemento == null ? "" : " – " + complemento)
                + " · " + bairro + " · " + cidade + " · CEP " + cep.substring(0, 5) + "-" + cep.substring(5);
    }

    private static String obrigatorio(String valor, int maximo, String mensagem) throws RegraNegocioException {
        if (Validacao.vazio(valor)) {
            throw new RegraNegocioException(mensagem);
        }
        String limpo = valor.trim().replaceAll("\\s+", " ");
        if (limpo.length() > maximo) {
            throw new RegraNegocioException(mensagem.replace("Informe", "Revise") + " (máximo de " + maximo
                    + " caracteres).");
        }
        return limpo;
    }

    private static BigDecimal dinheiro(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private void enviarConfirmacao(Usuario cliente, Pedido pedido) {
        try {
            emailService.enviarEmailAssincrono(cliente.getEmail(),
                    "Pedido " + pedido.getNumero() + " recebido - Vinheria Agnello",
                    EmailTemplateUtil.getCorpoConfirmacaoPedido(pedido, cliente.getNomeExibicao()));
        } catch (RuntimeException e) {
            // O pedido já está gravado: uma falha no e-mail não pode desfazer a compra
            LOGGER.log(Level.WARNING, "Falha ao enviar o e-mail do pedido " + pedido.getNumero(), e);
        }
    }

    // ===================================================================== consultar

    /**
     * Pedidos do cliente para a área pessoal. Um erro de banco sobe como
     * RuntimeException: o Controller mostra a página na mesma, com um aviso.
     */
    public List<Pedido> listarDoCliente(Usuario cliente) {
        if (cliente == null) {
            return new ArrayList<>();
        }
        return pedidoDAO.listarPorCliente(cliente.getId());
    }

    // ===================================================================== painel administrativo

    public static final int PEDIDOS_POR_PAGINA = 20;

    public List<Pedido> listarParaAdmin(StatusPedido status, String busca, int pagina) {
        int paginaValida = Math.max(1, pagina);
        // Pede um a mais para saber se existe próxima página
        return pedidoDAO.listarParaAdmin(status, busca, PEDIDOS_POR_PAGINA + 1, (paginaValida - 1) * PEDIDOS_POR_PAGINA);
    }

    public Map<StatusPedido, Integer> contarPorStatus() {
        return pedidoDAO.contarPorStatus();
    }

    public BigDecimal faturamentoDoMes() {
        return pedidoDAO.faturamentoDoMes();
    }

    public Pedido buscarParaAdmin(int id) {
        return id <= 0 ? null : pedidoDAO.buscarParaAdmin(id);
    }

    /**
     * Muda o status de um pedido pelo painel e avisa o cliente por e-mail.
     * @param statusVisto o status que o administrador via na tela (proteção contra
     *                    duas pessoas a mudar o mesmo pedido ao mesmo tempo)
     */
    public Pedido mudarStatus(Administrador admin, int pedidoId, String statusVisto, String statusNovo)
            throws RegraNegocioException {
        Pedido pedido = buscarParaAdmin(pedidoId);
        if (pedido == null) {
            throw new RegraNegocioException("Pedido não encontrado.");
        }
        StatusPedido novo = StatusPedido.doCodigo(statusNovo);
        StatusPedido visto = StatusPedido.doCodigo(statusVisto);
        if (visto != pedido.getStatus()) {
            throw new RegraNegocioException("Este pedido foi alterado por outra pessoa. Confira o status atual.");
        }
        if (novo == null || !pedido.getProximosStatus().contains(novo)) {
            throw new RegraNegocioException("Não é possível passar de \"" + pedido.getStatus().getDescricao()
                    + "\" para o status escolhido.");
        }

        pedidoDAO.mudarStatus(pedidoId, pedido.getStatus(), novo, admin.getId());
        LOGGER.log(Level.INFO, "Pedido {0}: {1} → {2} (administrador id={3})",
                new Object[] {pedido.getNumero(), pedido.getStatus(), novo, admin.getId()});
        pedido.setStatus(novo);

        if (pedido.getEmailCliente() != null) {
            try {
                emailService.enviarEmailAssincrono(pedido.getEmailCliente(),
                        "Pedido " + pedido.getNumero() + ": " + novo.getDescricao() + " - Vinheria Agnello",
                        EmailTemplateUtil.getCorpoMudancaStatus(pedido, pedido.getNomeCliente()));
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "Falha ao enviar o e-mail de status do pedido " + pedido.getNumero(), e);
            }
        }
        return pedido;
    }

    // ===================================================================== área pessoal do cliente

    /** O cliente cancela um pedido seu que ainda não foi pago. */
    public void cancelarPeloCliente(Usuario cliente, int pedidoId) throws RegraNegocioException {
        Pedido pedido = null;
        for (Pedido p : pedidoDAO.listarPorCliente(cliente.getId())) {
            if (p.getId() == pedidoId) {
                pedido = p;
            }
        }
        if (pedido == null) {
            throw new RegraNegocioException("Pedido não encontrado.");
        }
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO
                || !pedidoDAO.cancelarPeloCliente(pedidoId, cliente.getId())) {
            throw new RegraNegocioException("Só é possível cancelar pedidos que ainda aguardam pagamento. "
                    + "Para os outros, fale conosco.");
        }
        LOGGER.log(Level.INFO, "Pedido {0} cancelado pelo cliente id={1}",
                new Object[] {pedido.getNumero(), cliente.getId()});
        pedido.setStatus(StatusPedido.CANCELADO);
        try {
            emailService.enviarEmailAssincrono(cliente.getEmail(),
                    "Pedido " + pedido.getNumero() + " cancelado - Vinheria Agnello",
                    EmailTemplateUtil.getCorpoMudancaStatus(pedido, cliente.getNomeExibicao()));
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Falha ao enviar o e-mail de cancelamento", e);
        }
    }

    /**
     * "Comprar novamente": os vinhos de um pedido antigo, com o preço e o
     * estoque de HOJE. Os que já não estão à venda vêm em "indisponiveis".
     */
    public RepeticaoPedido repetir(Usuario cliente, int pedidoId) throws RegraNegocioException {
        Pedido pedido = null;
        for (Pedido p : pedidoDAO.listarPorCliente(cliente.getId())) {
            if (p.getId() == pedidoId) {
                pedido = p;
            }
        }
        if (pedido == null) {
            throw new RegraNegocioException("Pedido não encontrado.");
        }

        java.util.Set<Integer> ids = new java.util.LinkedHashSet<>();
        for (ItemPedido i : pedido.getItens()) {
            if (i.getProdutoId() != null) {
                ids.add(i.getProdutoId());
            }
        }
        Map<Integer, Produto> atuais = produtoDAO.buscarPorIds(ids);

        RepeticaoPedido r = new RepeticaoPedido();
        for (ItemPedido i : pedido.getItens()) {
            Produto p = i.getProdutoId() == null ? null : atuais.get(i.getProdutoId());
            if (p == null || !p.isAtivo() || p.getEstoque() <= 0) {
                r.getIndisponiveis().add(i.getNomeProduto());
            } else {
                r.getItens().add(new RepeticaoPedido.Item(p, Math.min(i.getQuantidade(), p.getEstoque())));
            }
        }
        return r;
    }
}

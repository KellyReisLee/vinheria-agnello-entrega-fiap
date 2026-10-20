package com.agnello.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.PedidoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Pedido;
import com.agnello.model.StatusPedido;
import com.agnello.model.Usuario;
import com.agnello.pagamento.ConfigPagamento;
import com.agnello.pagamento.ErroGateway;
import com.agnello.pagamento.GatewayPagamento;
import com.agnello.pagamento.MercadoPagoGateway;
import com.agnello.pagamento.PagamentoGateway;
import com.agnello.util.EmailTemplateUtil;

/**
 * Regras do pagamento online (Mercado Pago).
 *
 * Princípio de segurança: a loja NUNCA marca um pedido como pago com base no
 * que chega do navegador ou no conteúdo de um aviso. Recebe apenas o número do
 * pagamento e vai perguntar à API do Mercado Pago (com a credencial secreta)
 * qual é a situação real, de que pedido é e quanto foi pago.
 */
public class PagamentoService {

    private static final Logger LOGGER = Logger.getLogger(PagamentoService.class.getName());

    private final PedidoDAO pedidoDAO;
    private final GatewayPagamento gateway;
    private final EmailService emailService;
    private final boolean ativo;

    public PagamentoService() {
        this(new PedidoDAO(), new MercadoPagoGateway(), new EmailService(), ConfigPagamento.ativo());
    }

    /** Construtor para testes. */
    public PagamentoService(PedidoDAO pedidoDAO, GatewayPagamento gateway, EmailService emailService, boolean ativo) {
        this.pedidoDAO = pedidoDAO;
        this.gateway = gateway;
        this.emailService = emailService;
        this.ativo = ativo;
    }

    /** true quando o Mercado Pago está configurado (MP_ACCESS_TOKEN). */
    public boolean isAtivo() {
        return ativo;
    }

    // ===================================================================== iniciar

    /** Página de pagamento de um pedido acabado de criar no checkout. */
    public String iniciar(Usuario cliente, Pedido pedido, String urlBase) throws RegraNegocioException, ErroGateway {
        if (!ativo) {
            throw new RegraNegocioException("O pagamento online não está disponível no momento.");
        }
        if (cliente == null || pedido == null || pedido.getClienteId() == null
                || pedido.getClienteId() != cliente.getId()) {
            throw new RegraNegocioException("Pedido não encontrado.");
        }
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioException("Este pedido não está à espera de pagamento.");
        }
        return gateway.criarPagina(pedido, cliente.getEmail(), urlBase);
    }

    /** "Pagar agora" na área pessoal (ex.: o cliente fechou a página do Pix ou o cartão foi recusado). */
    public String iniciarPorId(Usuario cliente, int pedidoId, String urlBase) throws RegraNegocioException, ErroGateway {
        return iniciar(cliente, doCliente(cliente, pedidoId), urlBase);
    }

    // ===================================================================== confirmar

    /** O que aconteceu ao processar um pagamento. */
    public static final class Resultado {
        private final Pedido pedido;
        private final PagamentoGateway pagamento;
        private final boolean ficouPago;

        Resultado(Pedido pedido, PagamentoGateway pagamento, boolean ficouPago) {
            this.pedido = pedido;
            this.pagamento = pagamento;
            this.ficouPago = ficouPago;
        }

        /** Null se o pagamento não for de nenhum pedido nosso. */
        public Pedido getPedido() { return pedido; }
        public PagamentoGateway getPagamento() { return pagamento; }
        /** true só na primeira vez que o pagamento aprovado é processado. */
        public boolean isFicouPago() { return ficouPago; }
    }

    /**
     * Consulta o pagamento no Mercado Pago e atualiza o pedido. Pode ser chamado
     * várias vezes para o mesmo pagamento (aviso repetido, retorno do cliente):
     * só a primeira aprovação muda o pedido e envia o e-mail.
     */
    public Resultado processar(String pagamentoId) throws ErroGateway {
        PagamentoGateway pg = gateway.buscarPagamento(pagamentoId);
        if (pg == null) {
            LOGGER.log(Level.WARNING, "Pagamento {0} não existe no Mercado Pago", pagamentoId);
            return new Resultado(null, null, false);
        }
        int pedidoId;
        try {
            pedidoId = Integer.parseInt(pg.getReferenciaExterna());
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Pagamento {0} sem referência de pedido da loja", pg.getId());
            return new Resultado(null, pg, false);
        }
        Pedido pedido = pedidoDAO.buscarParaAdmin(pedidoId);
        if (pedido == null) {
            LOGGER.log(Level.WARNING, "Pagamento {0} aponta para o pedido id={1}, que não existe",
                    new Object[] {pg.getId(), pedidoId});
            return new Resultado(null, pg, false);
        }

        if (pg.isAprovado()) {
            // O valor pago tem de ser exatamente o total calculado pela loja
            if (pg.getValor() == null || pg.getValor().compareTo(pedido.getTotal()) != 0
                    || !"BRL".equals(pg.getMoeda())) {
                LOGGER.log(Level.SEVERE, "ATENÇÃO: pagamento {0} aprovado com valor {1} {2}, mas o pedido {3} "
                        + "custa {4}. O pedido NÃO foi marcado como pago; confira no painel do Mercado Pago.",
                        new Object[] {pg.getId(), pg.getValor(), pg.getMoeda(), pedido.getNumero(), pedido.getTotal()});
                pedidoDAO.registrarPagamento(pedido.getId(), pg.getId(), "valor_divergente");
                return new Resultado(pedido, pg, false);
            }

            boolean ficouPago = pedidoDAO.confirmarPagamento(pedido.getId(), pg.getId(), pg.getStatus());
            if (ficouPago) {
                LOGGER.log(Level.INFO, "Pedido {0} pago pelo Mercado Pago (pagamento {1}, {2})",
                        new Object[] {pedido.getNumero(), pg.getId(), pg.getMeio()});
                pedido.setStatus(StatusPedido.PAGO);
                avisarCliente(pedido);
            } else if (pedido.getStatus() == StatusPedido.CANCELADO) {
                LOGGER.log(Level.SEVERE, "ATENÇÃO: o pedido {0} está cancelado, mas o pagamento {1} foi aprovado. "
                        + "Faça a devolução no painel do Mercado Pago.", new Object[] {pedido.getNumero(), pg.getId()});
            }
            pedido.setPagamentoId(pg.getId());
            pedido.setPagamentoStatus(pg.getStatus());
            return new Resultado(pedido, pg, ficouPago);
        }

        // Não aprovado (Pix por pagar, cartão recusado, devolução…): guarda a situação.
        // Num pedido já pago, uma tentativa recusada antiga não pode apagar o pagamento bom;
        // só a devolução/contestação desse mesmo pagamento interessa.
        boolean mesmoPagamento = pg.getId().equals(pedido.getPagamentoId());
        if (pedido.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO || mesmoPagamento) {
            pedidoDAO.registrarPagamento(pedido.getId(), pg.getId(), pg.getStatus());
            pedido.setPagamentoId(pg.getId());
            pedido.setPagamentoStatus(pg.getStatus());
        }
        if (mesmoPagamento && (PagamentoGateway.DEVOLVIDO.equals(pg.getStatus())
                || PagamentoGateway.CONTESTADO.equals(pg.getStatus()))) {
            LOGGER.log(Level.WARNING, "Pagamento {0} do pedido {1} foi {2} no Mercado Pago",
                    new Object[] {pg.getId(), pedido.getNumero(), pg.getStatus()});
        }
        return new Resultado(pedido, pg, false);
    }

    /**
     * Cliente de volta do Mercado Pago (/pagamento/retorno). Confirma o pagamento
     * pela API (útil em localhost, onde os avisos do Mercado Pago não chegam) e
     * devolve o pedido atualizado.
     */
    public Resultado processarRetorno(Usuario cliente, int pedidoId, String pagamentoId)
            throws RegraNegocioException {
        Pedido pedido = doCliente(cliente, pedidoId);
        PagamentoGateway pg = null;
        if (ativo && pagamentoId != null && !pagamentoId.isBlank()) {
            try {
                Resultado r = processar(pagamentoId.trim());
                // Só vale se o pagamento for mesmo DESTE pedido
                if (r.getPedido() != null && r.getPedido().getId() == pedido.getId()) {
                    pg = r.getPagamento();
                }
            } catch (ErroGateway e) {
                LOGGER.log(Level.WARNING, "Não foi possível confirmar o pagamento no retorno do pedido "
                        + pedido.getNumero(), e);
            }
            pedido = doCliente(cliente, pedidoId); // relê do banco, já atualizado
        }
        return new Resultado(pedido, pg, false);
    }

    private Pedido doCliente(Usuario cliente, int pedidoId) throws RegraNegocioException {
        if (cliente != null) {
            for (Pedido p : pedidoDAO.listarPorCliente(cliente.getId())) {
                if (p.getId() == pedidoId) {
                    return p;
                }
            }
        }
        throw new RegraNegocioException("Pedido não encontrado.");
    }

    private void avisarCliente(Pedido pedido) {
        if (pedido.getEmailCliente() == null) {
            return;
        }
        try {
            emailService.enviarEmailAssincrono(pedido.getEmailCliente(),
                    "Pagamento aprovado - pedido " + pedido.getNumero() + " - Vinheria Agnello",
                    EmailTemplateUtil.getCorpoMudancaStatus(pedido, pedido.getNomeCliente()));
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Falha ao enviar o e-mail de pagamento aprovado do pedido "
                    + pedido.getNumero(), e);
        }
    }
}

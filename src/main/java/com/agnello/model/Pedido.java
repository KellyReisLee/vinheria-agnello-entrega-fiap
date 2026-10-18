package com.agnello.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Um pedido feito no checkout.
 *
 * Valores em BigDecimal (e não double): com dinheiro, 0.1 + 0.2 tem de dar
 * exatamente 0.30.
 */
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String numero;
    private Integer clienteId;
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;
    private FormaPagamento formaPagamento;
    private int parcelas = 1;
    private TipoEntrega tipoEntrega;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal frete = BigDecimal.ZERO;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    /** Endereço já formatado numa linha (no banco fica cifrado). Null na retirada. */
    private String endereco;
    private OffsetDateTime criadoEm;
    private List<ItemPedido> itens = new ArrayList<>();
    /** Só no painel: quem comprou (null se a conta foi excluída) e o histórico de status. */
    private String nomeCliente;
    private String emailCliente;
    private List<RegistroStatus> historico = new ArrayList<>();

    /** Status para os quais o painel pode mudar este pedido. */
    public List<StatusPedido> getProximosStatus() {
        return status.proximos(tipoEntrega);
    }

    /** Soma das quantidades (ex.: 2 Malbec + 1 Chablis = 3 garrafas). */
    public int getQuantidadeGarrafas() {
        int soma = 0;
        for (ItemPedido item : itens) {
            soma += item.getQuantidade();
        }
        return soma;
    }

    /** Valor de cada parcela no cartão. */
    public BigDecimal getValorParcela() {
        return total.divide(BigDecimal.valueOf(Math.max(1, parcelas)), 2, java.math.RoundingMode.HALF_UP);
    }

    /** Data como java.util.Date, que é o tipo que o fmt:formatDate do JSP entende. */
    public Date getDataCriacao() {
        return criadoEm == null ? null : Date.from(criadoEm.toInstant());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }

    public TipoEntrega getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(TipoEntrega tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getFrete() { return frete; }
    public void setFrete(BigDecimal frete) { this.frete = frete; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public List<RegistroStatus> getHistorico() { return historico; }
    public void setHistorico(List<RegistroStatus> historico) { this.historico = historico; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}

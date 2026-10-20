package com.agnello.pagamento;

import java.math.BigDecimal;

/**
 * Um pagamento como o Mercado Pago o descreve (GET /v1/payments/{id}).
 * Só guardamos o que precisamos para decidir o que fazer com o pedido.
 */
public class PagamentoGateway {

    /** Situações do Mercado Pago que interessam à loja. */
    public static final String APROVADO = "approved";
    public static final String PENDENTE = "pending";
    public static final String EM_ANALISE = "in_process";
    public static final String RECUSADO = "rejected";
    public static final String CANCELADO = "cancelled";
    public static final String DEVOLVIDO = "refunded";
    public static final String CONTESTADO = "charged_back";

    private final String id;
    private final String status;
    private final String statusDetalhe;
    private final String referenciaExterna;
    private final BigDecimal valor;
    private final String moeda;
    private final String meio;

    public PagamentoGateway(String id, String status, String statusDetalhe, String referenciaExterna,
            BigDecimal valor, String moeda, String meio) {
        this.id = id;
        this.status = status;
        this.statusDetalhe = statusDetalhe;
        this.referenciaExterna = referenciaExterna;
        this.valor = valor;
        this.moeda = moeda;
        this.meio = meio;
    }

    public boolean isAprovado() {
        return APROVADO.equals(status);
    }

    /** Pix ou boleto gerado, ou cartão em análise: ainda pode ser aprovado. */
    public boolean isPendente() {
        return PENDENTE.equals(status) || EM_ANALISE.equals(status);
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getStatusDetalhe() { return statusDetalhe; }
    /** O id do nosso pedido, enviado na criação do pagamento. */
    public String getReferenciaExterna() { return referenciaExterna; }
    public BigDecimal getValor() { return valor; }
    public String getMoeda() { return moeda; }
    /** Ex.: "pix", "visa", "bolbradesco". */
    public String getMeio() { return meio; }
}

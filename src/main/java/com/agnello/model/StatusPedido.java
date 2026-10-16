package com.agnello.model;

/**
 * Situações possíveis de um pedido. O nome (ex.: PAGO) é o que fica no banco;
 * a descrição é o texto mostrado ao cliente.
 */
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando pagamento", false),
    PAGO("Pagamento aprovado", true),
    EM_SEPARACAO("Em separação", true),
    ENVIADO("Enviado", true),
    PRONTO_RETIRADA("Pronto para retirada", true),
    ENTREGUE("Entregue", true),
    CANCELADO("Cancelado", false);

    private final String descricao;
    private final boolean positivo;

    StatusPedido(String descricao, boolean positivo) {
        this.descricao = descricao;
        this.positivo = positivo;
    }

    public String getDescricao() {
        return descricao;
    }

    /** true quando o pagamento já foi confirmado (o JSP pinta o selo de verde). */
    public boolean isPositivo() {
        return positivo;
    }
}

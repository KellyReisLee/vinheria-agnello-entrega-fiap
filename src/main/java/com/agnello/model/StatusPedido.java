package com.agnello.model;

import java.util.List;

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

    /** Nome no banco (ex.: "EM_SEPARACAO"); usado nos formulários do painel. */
    public String getCodigo() {
        return name();
    }

    /** Entregue e cancelado não mudam mais. */
    public boolean isFinal() {
        return this == ENTREGUE || this == CANCELADO;
    }

    /**
     * Para onde o pedido pode ir a partir deste status. O fluxo é sempre para a
     * frente, e "Enviado" só existe na entrega, "Pronto para retirada" só na
     * retirada. Cancelar é possível até o pedido sair da loja.
     *
     *   Aguardando pagamento → Pago → Em separação → Enviado / Pronto p/ retirada → Entregue
     */
    public List<StatusPedido> proximos(TipoEntrega entrega) {
        switch (this) {
            case AGUARDANDO_PAGAMENTO:
                return List.of(PAGO, CANCELADO);
            case PAGO:
                return List.of(EM_SEPARACAO, CANCELADO);
            case EM_SEPARACAO:
                return List.of(entrega == TipoEntrega.RETIRADA ? PRONTO_RETIRADA : ENVIADO, CANCELADO);
            case ENVIADO:
            case PRONTO_RETIRADA:
                return List.of(ENTREGUE);
            default:
                return List.of();
        }
    }

    /** Converte o texto vindo do formulário; null se não existir. */
    public static StatusPedido doCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        try {
            return valueOf(codigo.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

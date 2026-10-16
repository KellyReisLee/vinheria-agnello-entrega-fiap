package com.agnello.model;

/** Formas de pagamento aceitas. O código é o que o checkout.js envia. */
public enum FormaPagamento {
    PIX("pix", "Pix"),
    CARTAO("credit", "Cartão de crédito"),
    BOLETO("boleto", "Boleto bancário");

    private final String codigo;
    private final String descricao;

    FormaPagamento(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Converte o código enviado pelo navegador; devolve null se for desconhecido. */
    public static FormaPagamento doCodigo(String codigo) {
        for (FormaPagamento f : values()) {
            if (f.codigo.equalsIgnoreCase(codigo == null ? "" : codigo.trim())) {
                return f;
            }
        }
        return null;
    }
}

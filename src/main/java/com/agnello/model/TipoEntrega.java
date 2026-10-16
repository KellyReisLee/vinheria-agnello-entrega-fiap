package com.agnello.model;

/** Como o cliente recebe o pedido. */
public enum TipoEntrega {
    ENTREGA("Frete Térmico Agnello"),
    RETIRADA("Retirada na adega");

    private final String descricao;

    TipoEntrega(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Converte o valor enviado pelo navegador; devolve null se for desconhecido. */
    public static TipoEntrega doCodigo(String codigo) {
        for (TipoEntrega t : values()) {
            if (t.name().equalsIgnoreCase(codigo == null ? "" : codigo.trim())) {
                return t;
            }
        }
        return null;
    }
}

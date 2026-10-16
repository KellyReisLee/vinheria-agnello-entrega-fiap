package com.agnello.exception;

/** O cliente pediu mais garrafas do que há em estoque (ou o vinho esgotou durante a compra). */
public class EstoqueInsuficienteException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    public EstoqueInsuficienteException(String nomeProduto) {
        super("Não há estoque suficiente de \"" + nomeProduto + "\". Ajuste a quantidade no carrinho.");
    }
}

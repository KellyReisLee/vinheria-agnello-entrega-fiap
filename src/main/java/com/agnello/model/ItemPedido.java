package com.agnello.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Uma linha do pedido. Guarda nome e preço do vinho NO MOMENTO da compra:
 * se o preço do catálogo mudar depois, o pedido não muda.
 */
public class ItemPedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Integer produtoId;
    private String nomeProduto;
    private String imagem;
    private BigDecimal precoUnitario;
    private int quantidade;

    public ItemPedido() {
    }

    public ItemPedido(Integer produtoId, String nomeProduto, String imagem, BigDecimal precoUnitario, int quantidade) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.imagem = imagem;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    /** Preço × quantidade. */
    public BigDecimal getTotal() {
        return precoUnitario == null ? BigDecimal.ZERO : precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getProdutoId() { return produtoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}

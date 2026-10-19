package com.agnello.service;

import java.util.ArrayList;
import java.util.List;

import com.agnello.model.Produto;

/** Resultado do "Comprar novamente": o que pode voltar ao carrinho e o que não. */
public class RepeticaoPedido {

    /** Um vinho que pode voltar ao carrinho, com o preço de hoje. */
    public static final class Item {
        private final Produto produto;
        private final int quantidade;

        public Item(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public Produto getProduto() { return produto; }
        public int getQuantidade() { return quantidade; }
    }

    private final List<Item> itens = new ArrayList<>();
    private final List<String> indisponiveis = new ArrayList<>();

    public List<Item> getItens() { return itens; }
    public List<String> getIndisponiveis() { return indisponiveis; }
}

package com.agnello.model;

import java.io.Serializable;
import java.util.List;

/**
 * Uma página de resultados: os itens desta página + o total geral.
 * Ex.: 28 vinhos, 12 por página → página 2 tem 12 itens, total 28, 3 páginas.
 */
public class Pagina<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> itens;
    private final int total;
    private final int numero;
    private final int tamanho;

    public Pagina(List<T> itens, int total, int numero, int tamanho) {
        this.itens = itens;
        this.total = total;
        this.numero = numero;
        this.tamanho = tamanho;
    }

    public List<T> getItens() { return itens; }
    public int getTotal() { return total; }
    public int getNumero() { return numero; }
    public int getTamanho() { return tamanho; }

    public int getTotalPaginas() {
        return total == 0 ? 1 : (total + tamanho - 1) / tamanho;
    }

    public boolean isTemAnterior() { return numero > 1; }
    public boolean isTemProxima() { return numero < getTotalPaginas(); }

    /** Posição do 1.º e do último item desta página ("13–24 de 28"). */
    public int getPrimeiro() { return total == 0 ? 0 : (numero - 1) * tamanho + 1; }
    public int getUltimo() { return Math.min(numero * tamanho, total); }
}

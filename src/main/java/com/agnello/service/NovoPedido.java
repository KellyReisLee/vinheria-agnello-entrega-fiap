package com.agnello.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Os dados que o cliente enviou no checkout, ainda NÃO validados.
 *
 * Repare que aqui não há preço nenhum: o navegador só diz QUAIS vinhos e
 * QUANTAS garrafas. O preço vem sempre do banco (PedidoService), porque
 * qualquer pessoa pode alterar o JavaScript e mandar "R$ 0,01".
 */
public class NovoPedido {

    /** id do produto → quantidade (a ordem é a do carrinho). */
    private final Map<Integer, Integer> itens = new LinkedHashMap<>();
    private String tipoEntrega;
    private String formaPagamento;
    private int parcelas = 1;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    /** Soma quantidades se o mesmo vinho aparecer duas vezes. */
    public void adicionarItem(int produtoId, int quantidade) {
        itens.merge(produtoId, quantidade, Integer::sum);
    }

    public Map<Integer, Integer> getItens() { return itens; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
}

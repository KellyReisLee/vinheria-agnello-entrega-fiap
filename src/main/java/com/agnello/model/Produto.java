package com.agnello.model;


import java.io.Serializable;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String tipo;
    private String origem;
    private String descricao;
    private double preco;
    private double precoAntigo;
    private String desconto;
    private String pontuacao;
    private int estoque;
    private String imagem;

    // Construtor padrão (vazio)
    public Produto() {
    }

    // Construtor completo com todos os atributos
    public Produto(int id, String nome, String tipo, String origem, String descricao, 
                   double preco, double precoAntigo, String desconto, String pontuacao, 
                   int estoque, String imagem) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.origem = origem;
        this.descricao = descricao;
        this.preco = preco;
        this.precoAntigo = precoAntigo;
        this.desconto = desconto;
        this.pontuacao = pontuacao;
        this.estoque = estoque;
        this.imagem = imagem;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getPrecoAntigo() {
        return precoAntigo;
    }

    public void setPrecoAntigo(double precoAntigo) {
        this.precoAntigo = precoAntigo;
    }

    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        this.desconto = desconto;
    }

    public String getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(String pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @Override
    public String toString() {
        return "Produto [id=" + id + ", nome=" + nome + ", tipo=" + tipo + ", origem=" + origem 
                + ", preco=" + preco + ", estoque=" + estoque + "]";
    }
}
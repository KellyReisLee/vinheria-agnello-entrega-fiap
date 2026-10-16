package com.agnello.service;

import com.agnello.model.Produto;

/**
 * O que veio do formulário de vinho no painel, ainda como texto.
 *
 * Guardar o texto (e não já o número) permite mostrar de volta exatamente o
 * que a pessoa digitou quando há um erro ("189,9O" com a letra O) em vez de
 * apagar o campo.
 */
public class FormularioProduto {

    private Integer id;
    private String nome = "";
    private String tipo = "";
    private String origem = "";
    private String descricao = "";
    private String preco = "";
    private String precoAntigo = "";
    private String pontuacao = "";
    private String estoque = "0";
    private String imagemAtual = "";
    private boolean ativo = true;

    /** Preenche o formulário com um vinho que já existe (para editar). */
    public static FormularioProduto de(Produto p) {
        FormularioProduto f = new FormularioProduto();
        f.id = p.getId();
        f.nome = p.getNome();
        f.tipo = p.getTipo();
        f.origem = p.getOrigem() == null ? "" : p.getOrigem();
        f.descricao = p.getDescricao() == null ? "" : p.getDescricao();
        f.preco = formatar(p.getPreco());
        f.precoAntigo = p.getPrecoAntigo() > 0 ? formatar(p.getPrecoAntigo()) : "";
        f.pontuacao = p.getPontuacao() == null ? "" : p.getPontuacao();
        f.estoque = String.valueOf(p.getEstoque());
        f.imagemAtual = p.getImagem() == null ? "" : p.getImagem();
        f.ativo = p.isAtivo();
        return f;
    }

    /** 189.9 → "189,90" (formato que se digita no Brasil). */
    private static String formatar(double valor) {
        return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    public boolean isNovo() { return id == null; }

    /** Endereço da imagem atual pronto para o HTML. */
    public String getImagemUrl() {
        return imagemAtual.startsWith(".") ? imagemAtual.substring(1) : imagemAtual;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = limpo(nome); }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = limpo(tipo); }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = limpo(origem); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao == null ? "" : descricao.trim(); }

    public String getPreco() { return preco; }
    public void setPreco(String preco) { this.preco = limpo(preco); }

    public String getPrecoAntigo() { return precoAntigo; }
    public void setPrecoAntigo(String precoAntigo) { this.precoAntigo = limpo(precoAntigo); }

    public String getPontuacao() { return pontuacao; }
    public void setPontuacao(String pontuacao) { this.pontuacao = limpo(pontuacao); }

    public String getEstoque() { return estoque; }
    public void setEstoque(String estoque) { this.estoque = limpo(estoque); }

    public String getImagemAtual() { return imagemAtual; }
    public void setImagemAtual(String imagemAtual) { this.imagemAtual = limpo(imagemAtual); }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    private static String limpo(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }
}

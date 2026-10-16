package com.agnello.model;

import java.io.Serializable;

/** Mensagem enviada pelo formulário da página Contato. */
public class MensagemContato implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Integer clienteId;
    private String nome;
    private String email;
    private String assunto;
    private String numeroPedido;
    private String mensagem;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}

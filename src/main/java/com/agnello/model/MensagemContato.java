package com.agnello.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;

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
    private OffsetDateTime criadoEm;
    private boolean respondida;
    private String resposta;
    private OffsetDateTime respondidaEm;
    private String nomeAdministrador;

    /** Datas como java.util.Date, o tipo que o fmt:formatDate entende. */
    public Date getDataCriacao() {
        return criadoEm == null ? null : Date.from(criadoEm.toInstant());
    }

    public Date getDataResposta() {
        return respondidaEm == null ? null : Date.from(respondidaEm.toInstant());
    }

    /** Início da mensagem para a lista do painel. */
    public String getResumo() {
        if (mensagem == null) {
            return "";
        }
        String uma = mensagem.replaceAll("\\s+", " ");
        return uma.length() <= 110 ? uma : uma.substring(0, 110) + "…";
    }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public boolean isRespondida() { return respondida; }
    public void setRespondida(boolean respondida) { this.respondida = respondida; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }

    public OffsetDateTime getRespondidaEm() { return respondidaEm; }
    public void setRespondidaEm(OffsetDateTime respondidaEm) { this.respondidaEm = respondidaEm; }

    public String getNomeAdministrador() { return nomeAdministrador; }
    public void setNomeAdministrador(String nomeAdministrador) { this.nomeAdministrador = nomeAdministrador; }

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

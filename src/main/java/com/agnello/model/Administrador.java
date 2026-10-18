package com.agnello.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

/** Pessoa da equipe da loja que usa o painel /admin. */
public class Administrador implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private boolean ativo = true;
    /** Só existem durante o login; são apagados antes de ir para a sessão. */
    private transient String senhaHash;
    private transient OffsetDateTime bloqueadoAte;

    public void limparDadosDeAutenticacao() {
        senhaHash = null;
        bloqueadoAte = null;
    }

    /** "Kelly Reis" → "Kelly" (para a saudação do painel). */
    public String getPrimeiroNome() {
        return nome == null ? "" : nome.trim().split("\\s+")[0];
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public OffsetDateTime getBloqueadoAte() { return bloqueadoAte; }
    public void setBloqueadoAte(OffsetDateTime bloqueadoAte) { this.bloqueadoAte = bloqueadoAte; }
}

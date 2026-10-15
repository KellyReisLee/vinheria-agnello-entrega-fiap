package com.agnello.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Dados comuns a todos os clientes (PF e PJ).
 *
 * Este objeto fica guardado na sessão enquanto o cliente navega. Por isso:
 * - não existe campo "senha": a senha em texto nunca sai do pedido de login;
 * - senhaHash e bloqueadoAte são "transient": servem só para o login e nunca
 *   são gravados se o Tomcat persistir a sessão em disco. O ClienteService
 *   ainda os limpa antes de pôr o usuário na sessão.
 */
public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 2L;

    private int id;
    private String email;
    private String telefone;
    private boolean emailVerificado;

    private transient String senhaHash;
    private transient OffsetDateTime bloqueadoAte;

    protected Usuario() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public OffsetDateTime getBloqueadoAte() {
        return bloqueadoAte;
    }

    public void setBloqueadoAte(OffsetDateTime bloqueadoAte) {
        this.bloqueadoAte = bloqueadoAte;
    }

    /** Remove os dados que só servem para o login (chamado antes de ir para a sessão). */
    public void limparDadosDeAutenticacao() {
        this.senhaHash = null;
        this.bloqueadoAte = null;
    }

    /** "PF" ou "PJ". */
    public abstract String getTipoCliente();

    /** Nome para mostrar no site (nome completo ou razão social). */
    public abstract String getNomeExibicao();
}

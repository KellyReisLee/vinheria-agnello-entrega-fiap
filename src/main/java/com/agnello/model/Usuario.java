package com.agnello.model;

import java.io.Serializable;

public abstract class Usuario implements Serializable{
    private static final long serialVersionUID = 1L;
	protected int id;
    protected String email;
    protected String senha;
    protected String telefone;

    public Usuario() {}

    public Usuario(int id, String email, String senha, String telefone) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
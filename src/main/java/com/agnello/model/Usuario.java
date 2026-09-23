package com.agnello.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Usuario implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String email;
    private String senha;
    private String telefone;
    protected boolean emailVerificado;
    private String tokenAtivacao;
    private String tokenRecuperacao;
    private LocalDateTime tokenExpiracao;

    public Usuario() {}

	public Usuario(int id, String email, String senha, String telefone, boolean emailVerificado, String tokenAtivacao,
			String tokenRecuperacao, LocalDateTime tokenExpiracao) {
		super();
		this.id = id;
		this.email = email;
		this.senha = senha;
		this.telefone = telefone;
		this.emailVerificado = emailVerificado;
		this.tokenAtivacao = tokenAtivacao;
		this.tokenRecuperacao = tokenRecuperacao;
		this.tokenExpiracao = tokenExpiracao;
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

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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

	public String getTokenAtivacao() {
		return tokenAtivacao;
	}

	public void setTokenAtivacao(String tokenAtivacao) {
		this.tokenAtivacao = tokenAtivacao;
	}

	public String getTokenRecuperacao() {
		return tokenRecuperacao;
	}

	public void setTokenRecuperacao(String tokenRecuperacao) {
		this.tokenRecuperacao = tokenRecuperacao;
	}

	public LocalDateTime getTokenExpiracao() {
		return tokenExpiracao;
	}

	public void setTokenExpiracao(LocalDateTime tokenExpiracao) {
		this.tokenExpiracao = tokenExpiracao;
	}

	
	
    
}
package com.agnello.model;

/** Cliente pessoa física. O CPF chega aqui já decifrado pelo ClienteDAO. */
public class ClientePF extends Usuario {

    private static final long serialVersionUID = 2L;

    private String nome;
    private String sobrenome;
    private String cpf;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String getTipoCliente() {
        return "PF";
    }

    @Override
    public String getNomeExibicao() {
        return sobrenome == null || sobrenome.isBlank() ? nome : nome + " " + sobrenome;
    }
}

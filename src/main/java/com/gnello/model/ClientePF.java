package com.gnello.model;

public class ClientePF extends Usuario {
    private String nome;
    private String sobrenome;
    private String cpf;

    public ClientePF() {
        super();
    }

    public ClientePF(int id, String email, String senha, String telefone, String nome, String sobrenome, String cpf) {
        super(id, email, senha, telefone);
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
    }

    // Getters e Setters específicos
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
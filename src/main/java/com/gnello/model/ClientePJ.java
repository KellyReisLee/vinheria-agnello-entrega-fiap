package com.gnello.model;

public class ClientePJ extends Usuario {
    private String razaoSocial;
    private String cnpj;

    public ClientePJ() {
        super();
    }

    public ClientePJ(int id, String email, String senha, String telefone, String razaoSocial, String cnpj) {
        super(id, email, senha, telefone);
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
    }

    // Getters e Setters específicos
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
package com.agnello.model;

/** Cliente pessoa jurídica. */
public class ClientePJ extends Usuario {

    private static final long serialVersionUID = 2L;

    private String razaoSocial;
    private String cnpj;

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Override
    public String getTipoCliente() {
        return "PJ";
    }

    @Override
    public String getNomeExibicao() {
        return razaoSocial;
    }
}

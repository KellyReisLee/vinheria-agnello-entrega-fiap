package com.agnello.model;

import java.io.Serializable;

/** Endereço guardado na conta do cliente ("Casa", "Trabalho"). */
public class Endereco implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String apelido;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private boolean principal;

    /** "Av. Paulista, 1000 – apto 12 · Bela Vista · São Paulo - SP · CEP 01310-100" */
    public String getLinha() {
        return logradouro + ", " + numero + (complemento == null || complemento.isEmpty() ? "" : " – " + complemento)
                + " · " + bairro + " · " + cidade + " · CEP " + getCepFormatado();
    }

    public String getCepFormatado() {
        return cep != null && cep.length() == 8 ? cep.substring(0, 5) + "-" + cep.substring(5) : cep;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}

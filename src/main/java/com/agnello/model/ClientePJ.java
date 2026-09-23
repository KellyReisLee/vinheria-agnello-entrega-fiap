package com.agnello.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClientePJ extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String razaoSocial;
    private String cnpj;

    public ClientePJ() {
        super();
    }

    public ClientePJ(int id, String email, String senha, String telefone, boolean emailVerificado, 
                     String tokenAtivacao, String tokenRecuperacao, LocalDateTime tokenExpiracao, 
                     String razaoSocial, String cnpj) {
        super(id, email, senha, telefone, emailVerificado, tokenAtivacao, tokenRecuperacao, tokenExpiracao);
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
    }

    // Getters e Setters específicos
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
}
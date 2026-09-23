package com.agnello.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClientePF extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String sobrenome;
    private String cpf;

    public ClientePF() {
        super();
    }

    public ClientePF(int id, String email, String senha, String telefone, boolean emailVerificado, 
                     String tokenAtivacao, String tokenRecuperacao, LocalDateTime tokenExpiracao, 
                     String nome, String sobrenome, String cpf) {
        super(id, email, senha, telefone, emailVerificado, tokenAtivacao, tokenRecuperacao, tokenExpiracao);
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
    }

    // Getters e Setters específicos
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
}
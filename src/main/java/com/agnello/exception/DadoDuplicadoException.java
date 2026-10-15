package com.agnello.exception;

/** Lançada quando o e-mail, CPF ou CNPJ já pertence a outra conta. */
public class DadoDuplicadoException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    public DadoDuplicadoException(String mensagem) {
        super(mensagem);
    }
}

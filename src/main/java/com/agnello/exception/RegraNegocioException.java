package com.agnello.exception;

/**
 * Erro "esperado" cuja mensagem pode ser mostrada ao cliente
 * (ex.: "As senhas não coincidem", "Conta bloqueada por 15 minutos").
 *
 * Qualquer outra exceção (banco fora do ar, bug…) é registrada no log e o cliente
 * vê apenas uma mensagem genérica — nunca detalhes internos como erros de SQL.
 */
public class RegraNegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}

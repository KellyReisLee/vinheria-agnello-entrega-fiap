package com.agnello.pagamento;

/** Falha ao falar com o Mercado Pago (rede, credencial errada, resposta inesperada). */
public class ErroGateway extends Exception {

    private static final long serialVersionUID = 1L;

    public ErroGateway(String mensagem) {
        super(mensagem);
    }

    public ErroGateway(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

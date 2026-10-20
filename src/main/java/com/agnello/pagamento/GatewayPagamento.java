package com.agnello.pagamento;

import com.agnello.model.Pedido;

/**
 * O que a loja precisa de um serviço de pagamento. A implementação real é o
 * {@link MercadoPagoGateway}; nos testes usamos uma versão falsa, sem internet.
 */
public interface GatewayPagamento {

    /**
     * Cria a página de pagamento do pedido no Mercado Pago ("preferência") e
     * devolve o endereço para onde o cliente deve ir.
     *
     * @param urlBase endereço público do site (para os links de volta e o aviso de pagamento)
     * @throws ErroGateway se o Mercado Pago recusar ou não responder
     */
    String criarPagina(Pedido pedido, String emailCliente, String urlBase) throws ErroGateway;

    /** Consulta um pagamento no Mercado Pago. Null se não existir. */
    PagamentoGateway buscarPagamento(String pagamentoId) throws ErroGateway;
}

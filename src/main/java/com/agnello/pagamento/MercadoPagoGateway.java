package com.agnello.pagamento;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.model.FormaPagamento;
import com.agnello.model.Pedido;
import com.agnello.util.Json;
import com.agnello.util.RespostaJson;

/**
 * Integração com o Mercado Pago no modelo "Checkout Pro":
 *
 *   1. a loja cria uma "preferência" (a página de pagamento do pedido);
 *   2. o cliente paga na página segura do Mercado Pago (Pix, cartão ou boleto);
 *   3. o Mercado Pago avisa a loja (webhook) e devolve o cliente ao site.
 *
 * Os dados do cartão são digitados no site do Mercado Pago e nunca passam
 * pelo nosso servidor (é o que a norma PCI-DSS exige de uma loja pequena).
 *
 * Usa só a biblioteca padrão do Java (java.net.http), sem SDK.
 */
public class MercadoPagoGateway implements GatewayPagamento {

    private static final Logger LOGGER = Logger.getLogger(MercadoPagoGateway.class.getName());
    private static final String API = "https://api.mercadopago.com";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String token;

    public MercadoPagoGateway() {
        this(ConfigPagamento.token());
    }

    public MercadoPagoGateway(String token) {
        this.token = token;
    }

    @Override
    public String criarPagina(Pedido pedido, String emailCliente, String urlBase) throws ErroGateway {
        exigirToken();
        String corpo = RespostaJson.paraJson(montarPreferencia(pedido, emailCliente, urlBase));
        HttpRequest pedidoHttp = HttpRequest.newBuilder(URI.create(API + "/checkout/preferences"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                // Se a ligação cair e o pedido for repetido, o Mercado Pago não cria duas páginas
                .header("X-Idempotency-Key", "agnello-" + pedido.getId() + "-" + UUID.randomUUID())
                .POST(HttpRequest.BodyPublishers.ofString(corpo, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resposta = enviar(pedidoHttp);
        if (resposta.statusCode() / 100 != 2) {
            throw new ErroGateway("Mercado Pago recusou a criação do pagamento (HTTP " + resposta.statusCode()
                    + "): " + resumo(resposta.body()));
        }
        Map<String, Object> json = lerJson(resposta.body());
        String url = ConfigPagamento.modoTeste() && Json.texto(json, "sandbox_init_point") != null
                ? Json.texto(json, "sandbox_init_point")
                : Json.texto(json, "init_point");
        if (url == null || !url.startsWith("https://")) {
            throw new ErroGateway("Resposta do Mercado Pago sem endereço de pagamento.");
        }
        LOGGER.log(Level.INFO, "Página de pagamento criada para o pedido {0} (preferência {1})",
                new Object[] {pedido.getNumero(), Json.texto(json, "id")});
        return url;
    }

    @Override
    public PagamentoGateway buscarPagamento(String pagamentoId) throws ErroGateway {
        exigirToken();
        if (pagamentoId == null || !pagamentoId.matches("\\d{1,20}")) {
            return null; // ids do Mercado Pago são só números: qualquer outra coisa é ignorada
        }
        HttpRequest pedidoHttp = HttpRequest.newBuilder(URI.create(API + "/v1/payments/"
                + URLEncoder.encode(pagamentoId, StandardCharsets.UTF_8)))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> resposta = enviar(pedidoHttp);
        if (resposta.statusCode() == 404) {
            return null;
        }
        if (resposta.statusCode() / 100 != 2) {
            throw new ErroGateway("Mercado Pago não devolveu o pagamento " + pagamentoId + " (HTTP "
                    + resposta.statusCode() + ")");
        }
        return lerPagamento(lerJson(resposta.body()));
    }

    // ------------------------------------------------------------------ montagem (testável sem rede)

    /**
     * O corpo da preferência. Vai UM item com o total do pedido (já com frete e
     * desconto Pix), para o valor cobrado ser exatamente o que o nosso servidor
     * calculou. A forma de pagamento escolhida no checkout fica travada.
     */
    static Map<String, Object> montarPreferencia(Pedido pedido, String emailCliente, String urlBase) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", pedido.getNumero());
        item.put("title", "Pedido " + pedido.getNumero() + " - Vinheria Agnello");
        item.put("description", pedido.getQuantidadeGarrafas() + " garrafa(s) de vinho");
        item.put("category_id", "food");
        item.put("quantity", 1);
        item.put("currency_id", "BRL");
        item.put("unit_price", pedido.getTotal());

        Map<String, Object> pagador = new LinkedHashMap<>();
        pagador.put("email", emailCliente);

        String retorno = urlBase + "/pagamento/retorno?pedido=" + pedido.getId();
        Map<String, Object> voltas = new LinkedHashMap<>();
        voltas.put("success", retorno);
        voltas.put("pending", retorno);
        voltas.put("failure", retorno);

        Map<String, Object> meios = new LinkedHashMap<>();
        meios.put("excluded_payment_types", tiposExcluidos(pedido.getFormaPagamento()));
        if (pedido.getFormaPagamento() == FormaPagamento.CARTAO) {
            meios.put("installments", pedido.getParcelas());
            meios.put("default_installments", pedido.getParcelas());
        } else {
            meios.put("installments", 1);
        }

        Map<String, Object> p = new LinkedHashMap<>();
        p.put("items", List.of(item));
        p.put("payer", pagador);
        p.put("external_reference", String.valueOf(pedido.getId()));
        p.put("back_urls", voltas);
        p.put("payment_methods", meios);
        p.put("statement_descriptor", "VINHERIA AGNELLO");
        // O Mercado Pago só aceita voltar sozinho e avisar a loja em endereços públicos (https).
        // Em http://localhost o cliente volta pelo botão "Voltar à loja" e a página de
        // retorno confirma o pagamento consultando a API.
        if (urlBase.startsWith("https://")) {
            p.put("auto_return", "approved");
            p.put("notification_url", urlBase + "/pagamento/notificacao?source_news=webhooks");
        }
        return p;
    }

    /** Trava a forma escolhida no checkout (o desconto Pix só vale para Pix). */
    static List<String> tiposExcluidos(FormaPagamento forma) {
        List<String> excluidos = new ArrayList<>();
        switch (forma) {
            case PIX:
                excluidos.addAll(List.of("credit_card", "debit_card", "prepaid_card", "ticket", "atm"));
                break;
            case CARTAO:
                excluidos.addAll(List.of("debit_card", "prepaid_card", "ticket", "bank_transfer", "atm"));
                break;
            case BOLETO:
            default:
                excluidos.addAll(List.of("credit_card", "debit_card", "prepaid_card", "bank_transfer", "atm"));
                break;
        }
        return excluidos;
    }

    static PagamentoGateway lerPagamento(Map<String, Object> json) {
        return new PagamentoGateway(
                Json.texto(json, "id"),
                Json.texto(json, "status"),
                Json.texto(json, "status_detail"),
                Json.texto(json, "external_reference"),
                Json.numero(json, "transaction_amount"),
                Json.texto(json, "currency_id"),
                Json.texto(json, "payment_method_id"));
    }

    // ------------------------------------------------------------------ apoio

    private void exigirToken() throws ErroGateway {
        if (token == null) {
            throw new ErroGateway("Mercado Pago não configurado (falta " + ConfigPagamento.VAR_TOKEN + ").");
        }
    }

    private static HttpResponse<String> enviar(HttpRequest pedido) throws ErroGateway {
        try {
            return HTTP.send(pedido, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ErroGateway("Sem ligação com o Mercado Pago: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ErroGateway("Pedido ao Mercado Pago interrompido.", e);
        }
    }

    private static Map<String, Object> lerJson(String corpo) throws ErroGateway {
        try {
            return Json.lerObjeto(corpo);
        } catch (IllegalArgumentException e) {
            throw new ErroGateway("Resposta inválida do Mercado Pago.", e);
        }
    }

    /** Começo da resposta de erro (para o log), sem quebras de linha. */
    private static String resumo(String corpo) {
        if (corpo == null) {
            return "";
        }
        String s = corpo.replaceAll("\\s+", " ");
        return s.length() > 300 ? s.substring(0, 300) + "…" : s;
    }
}

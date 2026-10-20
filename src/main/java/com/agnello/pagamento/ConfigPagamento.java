package com.agnello.pagamento;

/**
 * Credenciais do Mercado Pago, lidas das variáveis de ambiente (nunca do código):
 *
 *   MP_ACCESS_TOKEN     obrigatória. "Access Token" das credenciais (de teste ou de produção)
 *   MP_WEBHOOK_SECRET   recomendada. "Assinatura secreta" das notificações (Webhooks)
 *
 * Sem MP_ACCESS_TOKEN o site continua a funcionar: os pedidos ficam
 * "Aguardando pagamento" e a loja confirma-os à mão no painel.
 */
public final class ConfigPagamento {

    public static final String VAR_TOKEN = "MP_ACCESS_TOKEN";
    public static final String VAR_SEGREDO_WEBHOOK = "MP_WEBHOOK_SECRET";

    private ConfigPagamento() {
    }

    public static String token() {
        return limpo(System.getenv(VAR_TOKEN));
    }

    public static String segredoWebhook() {
        return limpo(System.getenv(VAR_SEGREDO_WEBHOOK));
    }

    /** true quando o pagamento online está ligado. */
    public static boolean ativo() {
        return token() != null;
    }

    /** Credenciais de teste antigas começam por "TEST-": o pagamento abre no ambiente de testes. */
    public static boolean modoTeste() {
        String t = token();
        return t != null && t.startsWith("TEST-");
    }

    private static String limpo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

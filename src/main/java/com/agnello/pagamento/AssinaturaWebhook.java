package com.agnello.pagamento;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * Confere se um aviso (webhook) veio mesmo do Mercado Pago.
 *
 * O Mercado Pago envia o cabeçalho  x-signature: ts=1742505638683,v1=ced36ab6...
 * O v1 é um HMAC-SHA256, com a "assinatura secreta" da aplicação, deste texto:
 *
 *     id:{data.id};request-id:{x-request-id};ts:{ts};
 *
 * Mesmo com a assinatura certa, a loja nunca confia no conteúdo do aviso: vai
 * sempre buscar o pagamento à API do Mercado Pago (ver PagamentoService).
 */
public final class AssinaturaWebhook {

    private AssinaturaWebhook() {
    }

    public static boolean valida(String cabecalhoAssinatura, String requestId, String dataId, String segredo) {
        if (cabecalhoAssinatura == null || segredo == null || dataId == null) {
            return false;
        }
        String ts = null;
        String v1 = null;
        for (String parte : cabecalhoAssinatura.split(",")) {
            String[] kv = parte.trim().split("=", 2);
            if (kv.length == 2) {
                if ("ts".equals(kv[0].trim())) {
                    ts = kv[1].trim();
                } else if ("v1".equals(kv[0].trim())) {
                    v1 = kv[1].trim();
                }
            }
        }
        if (ts == null || v1 == null) {
            return false;
        }

        StringBuilder modelo = new StringBuilder();
        // ids com letras são comparados em minúsculas (regra do Mercado Pago)
        modelo.append("id:").append(dataId.toLowerCase(Locale.ROOT)).append(';');
        if (requestId != null && !requestId.isBlank()) {
            modelo.append("request-id:").append(requestId.trim()).append(';');
        }
        modelo.append("ts:").append(ts).append(';');

        String esperado = hmac(segredo, modelo.toString());
        // Comparação em tempo constante: não revela quantos caracteres acertaram
        return MessageDigest.isEqual(esperado.getBytes(StandardCharsets.US_ASCII),
                v1.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
    }

    static String hmac(String segredo, String texto) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 indisponível", e);
        }
    }
}

package com.agnello.seguranca;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * "Índice cego" (blind index): HMAC-SHA256 de um valor, com uma chave secreta.
 *
 * Problema: o CPF fica cifrado com IV aleatório, então o mesmo CPF gera textos
 * cifrados diferentes — o banco não consegue garantir "CPF único" nem procurar por CPF.
 * Solução: guardamos também o HMAC do CPF. É sempre igual para o mesmo CPF (permite
 * UNIQUE e WHERE), mas sem a chave não dá para descobrir o CPF a partir dele.
 */
public final class IndiceCego {

    private IndiceCego() {
    }

    /** Devolve 64 caracteres hexadecimais. O nome do campo evita colisões entre colunas. */
    public static String calcular(String campo, String valorNormalizado) {
        if (valorNormalizado == null) {
            return null;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(ChavesAplicacao.chaveIndice(), "HmacSHA256"));
            byte[] resultado = mac.doFinal((campo + ":" + valorNormalizado).getBytes(StandardCharsets.UTF_8));
            return Hex.de(resultado);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 indisponível nesta JVM.", e);
        }
    }
}

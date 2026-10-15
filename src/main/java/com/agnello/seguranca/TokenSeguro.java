package com.agnello.seguranca;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Tokens de uso único (ativação de conta e recuperação de senha).
 *
 * - O token tem 256 bits aleatórios (impossível de adivinhar), em Base64 "URL safe".
 * - Só o SHA-256 do token vai para o banco. O token verdadeiro existe apenas no link
 *   do e-mail: quem ler o banco não consegue usá-lo para entrar na conta de ninguém.
 *   (SHA-256 simples chega aqui porque o token já é aleatório e longo; senhas precisam
 *   de PBKDF2 porque são curtas e previsíveis.)
 */
public final class TokenSeguro {

    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenSeguro() {
    }

    public static String gerar() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String token) {
        if (token == null) {
            return null;
        }
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return Hex.de(sha.digest(token.trim().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível nesta JVM.", e);
        }
    }
}

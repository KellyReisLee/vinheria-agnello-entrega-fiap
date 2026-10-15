package com.agnello.seguranca;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de senhas com PBKDF2-HMAC-SHA256 (incluído no Java, sem bibliotecas extra).
 *
 * Porquê hash e não criptografia? Uma senha nunca precisa de ser "decifrada":
 * no login calculamos o hash do que a pessoa digitou e comparamos.
 *
 * Formato guardado na coluna senha_hash (inclui tudo o que é preciso para verificar):
 *   pbkdf2_sha256$600000$<salt Base64>$<hash Base64>
 *
 * - 600 000 iterações: valor recomendado pela OWASP para PBKDF2-SHA256.
 *   Torna cada tentativa lenta para quem tenta adivinhar senhas em massa.
 * - Salt aleatório de 16 bytes por senha: duas pessoas com a mesma senha
 *   ficam com hashes diferentes (impede tabelas pré-calculadas).
 * - Se um dia aumentarmos as iterações, precisaAtualizar() avisa e o login
 *   grava o hash novo automaticamente.
 */
public final class HashSenha {

    private static final String ALGORITMO = "pbkdf2_sha256";
    private static final String ALGORITMO_JAVA = "PBKDF2WithHmacSHA256";
    public static final int ITERACOES = 600_000;
    private static final int TAMANHO_SALT = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Hash calculado uma vez, usado quando o e-mail não existe (ver verificarSemUsuario). */
    private static volatile String hashFicticio;

    private HashSenha() {
    }

    public static String gerar(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(senha, salt, ITERACOES);
        Base64.Encoder b64 = Base64.getEncoder().withoutPadding();
        return ALGORITMO + "$" + ITERACOES + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenado) {
        if (senha == null || armazenado == null) {
            return false;
        }
        String[] partes = armazenado.split("\\$");
        if (partes.length != 4 || !ALGORITMO.equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = pbkdf2(senha, salt, iteracoes);
            // Comparação em tempo constante: não revela "quantos bytes acertou"
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** true quando o hash foi gerado com parâmetros mais fracos que os atuais. */
    public static boolean precisaAtualizar(String armazenado) {
        String[] partes = armazenado == null ? new String[0] : armazenado.split("\\$");
        if (partes.length != 4 || !ALGORITMO.equals(partes[0])) {
            return true;
        }
        try {
            return Integer.parseInt(partes[1]) < ITERACOES;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * Gasta o mesmo tempo de uma verificação real. Usado quando o e-mail não existe,
     * para que o tempo de resposta não revele quais e-mails estão cadastrados.
     */
    public static void verificarSemUsuario(String senha) {
        if (hashFicticio == null) {
            hashFicticio = gerar("senha-ficticia-para-igualar-o-tempo");
        }
        verificar(senha == null ? "" : senha, hashFicticio);
    }

    private static byte[] pbkdf2(String senha, byte[] salt, int iteracoes) {
        char[] caracteres = senha.toCharArray();
        try {
            KeySpec spec = new PBEKeySpec(caracteres, salt, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO_JAVA).generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 indisponível nesta JVM.", e);
        } finally {
            java.util.Arrays.fill(caracteres, '\0');
        }
    }

}

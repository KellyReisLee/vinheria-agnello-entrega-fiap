package com.agnello.seguranca;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Criptografia de dados pessoais com AES-256-GCM (padrão da indústria para dados em repouso).
 *
 * Diferente do hash, aqui precisamos de recuperar o valor original (ex.: mostrar o CPF
 * na área pessoal), por isso é criptografia reversível — mas só com a chave, que vive
 * numa variável de ambiente, fora do banco.
 *
 * Formato guardado:  v1:<Base64( IV de 12 bytes | texto cifrado | tag de 16 bytes )>
 *  - "v1" identifica a versão da chave, para permitir trocar a chave no futuro.
 *  - IV aleatório em cada operação: o mesmo CPF cifrado duas vezes dá resultados diferentes.
 *  - GCM é "autenticado": se alguém alterar o valor no banco, decifrar() falha.
 *  - O nome do campo entra como "dados associados" (AAD): um valor cifrado da coluna
 *    telefone não pode ser copiado para a coluna cpf e ser aceito.
 */
public final class CifraDados {

    private static final String PREFIXO = "v1:";
    private static final String TRANSFORMACAO = "AES/GCM/NoPadding";
    private static final int TAMANHO_IV = 12;
    private static final int TAMANHO_TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CifraDados() {
    }

    public static String cifrar(String campo, String texto) {
        if (texto == null) {
            return null;
        }
        try {
            byte[] iv = new byte[TAMANHO_IV];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            cipher.init(Cipher.ENCRYPT_MODE, chave(), new GCMParameterSpec(TAMANHO_TAG_BITS, iv));
            cipher.updateAAD(campo.getBytes(StandardCharsets.UTF_8));
            byte[] cifrado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

            ByteBuffer pacote = ByteBuffer.allocate(iv.length + cifrado.length);
            pacote.put(iv).put(cifrado);
            return PREFIXO + Base64.getEncoder().encodeToString(pacote.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao cifrar o campo " + campo, e);
        }
    }

    public static String decifrar(String campo, String valorCifrado) {
        if (valorCifrado == null) {
            return null;
        }
        if (!valorCifrado.startsWith(PREFIXO)) {
            throw new IllegalStateException("Formato de dado cifrado desconhecido no campo " + campo);
        }
        try {
            byte[] pacote = Base64.getDecoder().decode(valorCifrado.substring(PREFIXO.length()));
            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            cipher.init(Cipher.DECRYPT_MODE, chave(), new GCMParameterSpec(TAMANHO_TAG_BITS, pacote, 0, TAMANHO_IV));
            cipher.updateAAD(campo.getBytes(StandardCharsets.UTF_8));
            byte[] texto = cipher.doFinal(pacote, TAMANHO_IV, pacote.length - TAMANHO_IV);
            return new String(texto, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            // Chave errada ou valor adulterado
            throw new IllegalStateException("Não foi possível decifrar o campo " + campo
                    + " (chave incorreta ou dado alterado).", e);
        }
    }

    private static SecretKeySpec chave() {
        return new SecretKeySpec(ChavesAplicacao.chaveDados(), "AES");
    }
}

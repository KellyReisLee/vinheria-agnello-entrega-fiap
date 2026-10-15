package com.agnello.seguranca;

import java.util.Base64;

/**
 * Lê as chaves criptográficas da aplicação a partir de variáveis de ambiente.
 *
 * As chaves NUNCA ficam no código nem no banco de dados: quem roubar uma cópia
 * do banco não consegue decifrar os dados sem elas.
 *
 *   AGNELLO_CHAVE_DADOS   → AES-256 (32 bytes em Base64): cifra CPF e telefone
 *   AGNELLO_CHAVE_INDICE  → HMAC-SHA256 (32 bytes em Base64): índice cego do CPF
 *
 * Gere chaves novas com: com.agnello.migracao.GerarChaves (Run As → Java Application).
 * Também aceita -DAGNELLO_CHAVE_DADOS=... (propriedade do sistema), útil em testes.
 */
public final class ChavesAplicacao {

    public static final String VAR_CHAVE_DADOS = "AGNELLO_CHAVE_DADOS";
    public static final String VAR_CHAVE_INDICE = "AGNELLO_CHAVE_INDICE";

    private static volatile byte[] chaveDados;
    private static volatile byte[] chaveIndice;

    private ChavesAplicacao() {
    }

    static byte[] chaveDados() {
        if (chaveDados == null) {
            chaveDados = carregar(VAR_CHAVE_DADOS);
        }
        return chaveDados;
    }

    static byte[] chaveIndice() {
        if (chaveIndice == null) {
            chaveIndice = carregar(VAR_CHAVE_INDICE);
        }
        return chaveIndice;
    }

    /** Confirma no arranque que as duas chaves existem e são válidas (falha cedo). */
    public static void validar() {
        chaveDados();
        chaveIndice();
    }

    private static byte[] carregar(String nome) {
        String valor = System.getProperty(nome);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(nome);
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("A variável de ambiente " + nome + " não está configurada. "
                    + "Gere uma chave com com.agnello.migracao.GerarChaves e configure-a no servidor.");
        }
        byte[] chave;
        try {
            chave = Base64.getDecoder().decode(valor.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("A variável " + nome + " não está em Base64 válido.", e);
        }
        if (chave.length != 32) {
            throw new IllegalStateException("A variável " + nome + " deve ter 32 bytes (256 bits); tem " + chave.length + ".");
        }
        return chave;
    }
}

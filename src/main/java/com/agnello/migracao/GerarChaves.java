package com.agnello.migracao;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Gera as duas chaves secretas da aplicação.
 *
 * Como usar (uma única vez por ambiente):
 *   Eclipse → botão direito neste arquivo → Run As → Java Application.
 *   Copie as duas linhas do Console para as variáveis de ambiente
 *   (Tomcat no Eclipse e Render → Environment).
 *
 * IMPORTANTE:
 * - Guarde as chaves num gestor de senhas. Se perder a AGNELLO_CHAVE_DADOS,
 *   os CPFs e telefones cifrados não podem ser recuperados.
 * - Nunca as coloque no código, no Git nem em prints/mensagens.
 * - Use as MESMAS chaves no Eclipse e no Render se ambos usarem o mesmo banco.
 */
public final class GerarChaves {

    private GerarChaves() {
    }

    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        System.out.println("AGNELLO_CHAVE_DADOS=" + novaChave(random));
        System.out.println("AGNELLO_CHAVE_INDICE=" + novaChave(random));
    }

    private static String novaChave(SecureRandom random) {
        byte[] chave = new byte[32];
        random.nextBytes(chave);
        return Base64.getEncoder().encodeToString(chave);
    }
}

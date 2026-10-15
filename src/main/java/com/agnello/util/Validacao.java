package com.agnello.util;

import java.util.regex.Pattern;

/**
 * Validações de dados de cadastro feitas no servidor.
 *
 * O JavaScript do site já valida, mas qualquer pessoa pode enviar um pedido
 * direto ao servidor sem passar pelo navegador — por isso validamos de novo aqui.
 */
public final class Validacao {

    /** Mínimo recomendado pelo NIST (SP 800-63B) para senhas escolhidas pelo usuário. */
    public static final int SENHA_MIN = 8;
    /** Limite superior evita que alguém envie "senhas" gigantes para sobrecarregar o servidor. */
    public static final int SENHA_MAX = 128;

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");

    private Validacao() {
    }

    /** E-mail em minúsculas e sem espaços (a mesma pessoa não cria duas contas por causa de maiúsculas). */
    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public static boolean emailValido(String email) {
        return email != null && email.length() <= 254 && EMAIL.matcher(email).matches();
    }

    /** Remove tudo o que não é dígito: "123.456.789-09" → "12345678909". */
    public static String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    public static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean cpfValido(String cpf) {
        String d = somenteDigitos(cpf);
        if (d == null || d.length() != 11 || d.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(d, 9, 10) == d.charAt(9) - '0'
                && digitoVerificador(d, 10, 11) == d.charAt(10) - '0';
    }

    private static int digitoVerificador(String d, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (d.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    public static boolean cnpjValido(String cnpj) {
        String d = somenteDigitos(cnpj);
        if (d == null || d.length() != 14 || d.chars().distinct().count() == 1) {
            return false;
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digitoCnpj(d, pesos1) == d.charAt(12) - '0'
                && digitoCnpj(d, pesos2) == d.charAt(13) - '0';
    }

    private static int digitoCnpj(String d, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (d.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /** Telefone brasileiro com DDD: 10 (fixo) ou 11 (celular) dígitos. */
    public static boolean telefoneValido(String telefone) {
        String d = somenteDigitos(telefone);
        return d != null && (d.length() == 10 || d.length() == 11);
    }

    public static String validarSenha(String senha) {
        if (senha == null || senha.length() < SENHA_MIN) {
            return "A senha deve ter pelo menos " + SENHA_MIN + " caracteres.";
        }
        if (senha.length() > SENHA_MAX) {
            return "A senha pode ter no máximo " + SENHA_MAX + " caracteres.";
        }
        return null;
    }
}

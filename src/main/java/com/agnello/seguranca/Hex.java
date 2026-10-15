package com.agnello.seguranca;

/** Converte bytes em texto hexadecimal minúsculo (ex.: 0x0f → "0f"). */
final class Hex {

    private static final char[] DIGITOS = "0123456789abcdef".toCharArray();

    private Hex() {
    }

    static String de(byte[] bytes) {
        char[] saida = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            saida[i * 2] = DIGITOS[v >>> 4];
            saida[i * 2 + 1] = DIGITOS[v & 0x0F];
        }
        return new String(saida);
    }
}

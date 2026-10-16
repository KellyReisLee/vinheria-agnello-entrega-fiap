package com.agnello.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limita quantas vezes a mesma "chave" (ex.: o IP) pode fazer uma ação numa
 * janela de tempo. Ex.: no máximo 5 mensagens de contato a cada 10 minutos.
 *
 * Fica na memória do servidor: é simples e suficiente para um único servidor
 * (como no Render). Com vários servidores, usaríamos algo partilhado (Redis).
 */
public class LimiteTentativas {

    private final int maximo;
    private final long janelaMillis;
    private final Map<String, Deque<Long>> registros = new ConcurrentHashMap<>();

    public LimiteTentativas(int maximo, long janelaMillis) {
        this.maximo = maximo;
        this.janelaMillis = janelaMillis;
    }

    /** Regista a tentativa e diz se ela é permitida. */
    public boolean permitir(String chave) {
        return permitir(chave, System.currentTimeMillis());
    }

    /** Versão com o "agora" como parâmetro, para os testes não dependerem do relógio. */
    boolean permitir(String chave, long agora) {
        if (registros.size() > 10_000) {
            limparAntigos(agora); // não deixa a memória crescer sem fim
        }
        Deque<Long> tentativas = registros.computeIfAbsent(chave, k -> new ArrayDeque<>());
        synchronized (tentativas) {
            while (!tentativas.isEmpty() && agora - tentativas.peekFirst() >= janelaMillis) {
                tentativas.pollFirst();
            }
            if (tentativas.size() >= maximo) {
                return false;
            }
            tentativas.addLast(agora);
            return true;
        }
    }

    private void limparAntigos(long agora) {
        registros.entrySet().removeIf(e -> {
            synchronized (e.getValue()) {
                Long ultimo = e.getValue().peekLast();
                return ultimo == null || agora - ultimo >= janelaMillis;
            }
        });
    }
}

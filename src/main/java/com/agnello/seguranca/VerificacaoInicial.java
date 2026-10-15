package com.agnello.seguranca;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Roda uma vez quando o Tomcat inicia a aplicação e confere se as chaves de
 * criptografia estão configuradas. Se faltar alguma, o problema aparece logo no
 * log (e não só quando o primeiro cliente tentar fazer login).
 */
@WebListener
public class VerificacaoInicial implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(VerificacaoInicial.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        try {
            ChavesAplicacao.validar();
            LOGGER.info("Chaves de criptografia carregadas.");
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "CONFIGURAÇÃO EM FALTA: " + e.getMessage()
                    + " Cadastro e login não vão funcionar até isto ser corrigido.");
        }
    }
}

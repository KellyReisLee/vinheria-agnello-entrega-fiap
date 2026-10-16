package com.agnello.migracao;

import java.io.Console;
import java.util.Scanner;

import com.agnello.exception.RegraNegocioException;
import com.agnello.service.AdministradorService;

/**
 * Cria a conta de administrador do painel /admin (ou troca a senha de uma
 * conta que já existe). Não há cadastro de administrador pelo site: só quem
 * tem acesso ao código e às variáveis do banco consegue criar um.
 *
 * Como usar no Eclipse:
 *   1. Run → Run Configurations… → Java Application → New
 *   2. Main class: com.agnello.migracao.CriarAdministrador
 *   3. Separador Environment: NEON_DB_URL, NEON_DB_USER e NEON_DB_PASSWORD
 *      (as mesmas do Tomcat)
 *   4. Separador Arguments: o e-mail e o nome entre aspas, por exemplo:
 *        kelly@exemplo.com "Kelly Reis"
 *   5. Run. O Console pede a senha (mínimo 12 caracteres).
 */
public final class CriarAdministrador {

    /** Um único leitor do teclado: dois Scanner no System.in perdem linhas. */
    private static final Scanner TECLADO = new Scanner(System.in);

    private CriarAdministrador() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: CriarAdministrador <email> \"<nome>\"");
            return;
        }
        String email = args[0];
        String nome = args[1];

        String senha = lerSenha("Senha do administrador (mín. " + AdministradorService.SENHA_MIN_ADMIN + " caracteres): ");
        String confirmacao = lerSenha("Repita a senha: ");
        if (!senha.equals(confirmacao)) {
            System.out.println("As senhas não coincidem. Nada foi gravado.");
            return;
        }

        try {
            boolean criou = new AdministradorService().criarOuRedefinir(nome, email, senha);
            System.out.println(criou
                    ? "Administrador criado. Entre em /admin/login com este e-mail."
                    : "Este e-mail já era administrador: a senha foi trocada e a conta reativada.");
        } catch (RegraNegocioException e) {
            System.out.println("Não foi possível: " + e.getMessage());
        }
    }

    /**
     * No Terminal a senha não aparece enquanto se digita (System.console).
     * O Console do Eclipse não suporta isso; aí a senha aparece na tela,
     * por isso limpe o Console depois de usar.
     */
    private static String lerSenha(String pergunta) {
        Console console = System.console();
        if (console != null) {
            char[] senha = console.readPassword(pergunta);
            return senha == null ? "" : new String(senha);
        }
        System.out.print(pergunta);
        return TECLADO.hasNextLine() ? TECLADO.nextLine() : "";
    }
}

-- =============================================================================
-- V2 — Prepara a tabela "clientes" para guardar dados protegidos
-- =============================================================================
-- Etapa 1 de 3 da migração (padrão "expandir → migrar → contrair"):
--   V2 (este arquivo)      adiciona as colunas novas, sem apagar nada
--   MigrarDadosSensiveis    programa Java que preenche as colunas novas
--   V3                      apaga as colunas antigas em texto puro
--
-- Pode ser executado mais de uma vez sem problema (IF NOT EXISTS).
-- Onde executar: Neon → SQL Editor (ou psql), no banco da aplicação.
-- =============================================================================

BEGIN;

ALTER TABLE clientes
    -- Senha: só o hash PBKDF2 (nunca a senha)
    ADD COLUMN IF NOT EXISTS senha_hash               VARCHAR(255),

    -- CPF: cifrado com AES-256-GCM + índice cego (HMAC) para unicidade e busca
    ADD COLUMN IF NOT EXISTS cpf_cifrado              TEXT,
    ADD COLUMN IF NOT EXISTS cpf_indice               CHAR(64),

    -- Telefone: cifrado com AES-256-GCM
    ADD COLUMN IF NOT EXISTS telefone_cifrado         TEXT,

    -- Tokens: só o SHA-256, com prazo de validade
    ADD COLUMN IF NOT EXISTS token_ativacao_hash      CHAR(64),
    ADD COLUMN IF NOT EXISTS token_ativacao_expira    TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS token_recuperacao_hash   CHAR(64),
    ADD COLUMN IF NOT EXISTS token_recuperacao_expira TIMESTAMPTZ,

    -- Proteção contra tentativas repetidas de senha (bloqueio temporário)
    ADD COLUMN IF NOT EXISTS tentativas_login_falhas  INTEGER     NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS bloqueado_ate            TIMESTAMPTZ,

    -- Auditoria básica
    ADD COLUMN IF NOT EXISTS ultimo_login_em          TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS criado_em                TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS atualizado_em            TIMESTAMPTZ NOT NULL DEFAULT now();

-- As colunas antigas deixam de ser obrigatórias: a versão nova da aplicação
-- já não as preenche (e serão apagadas no V3).
-- (o IF protege o caso de o script ser executado de novo depois do V3)
DO $$
DECLARE
    coluna TEXT;
BEGIN
    FOREACH coluna IN ARRAY ARRAY['senha', 'cpf', 'telefone'] LOOP
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'clientes' AND column_name = coluna) THEN
            EXECUTE format('ALTER TABLE clientes ALTER COLUMN %I DROP NOT NULL', coluna);
        END IF;
    END LOOP;
END $$;

COMMIT;

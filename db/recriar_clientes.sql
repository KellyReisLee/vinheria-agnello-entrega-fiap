-- =============================================================================
-- RECRIAR A TABELA "clientes" DO ZERO (apaga TODOS os clientes)
-- =============================================================================
-- Use isto em vez da migração V2 → MigrarDadosSensiveis → V3 quando os clientes
-- atuais são só contas de teste e podem ser descartados.
--
-- O que faz:
--   1. apaga a tabela clientes antiga (com todas as contas, senhas em texto puro etc.)
--   2. cria a tabela nova, já no formato protegido (senha em hash, CPF e telefone cifrados)
-- O que NÃO faz: não mexe na tabela produtos (o catálogo continua igual).
--
-- ATENÇÃO: não tem como desfazer. Crie antes um branch de backup no Neon
-- (Branches → Create branch).
-- =============================================================================

BEGIN;

DROP TABLE IF EXISTS clientes CASCADE;

CREATE TABLE clientes (
    id                        SERIAL       PRIMARY KEY,
    tipo_cliente              VARCHAR(2)   NOT NULL,
    email                     VARCHAR(254) NOT NULL,
    senha_hash                VARCHAR(255) NOT NULL,

    -- Pessoa física
    nome                      VARCHAR(100),
    sobrenome                 VARCHAR(100),
    cpf_cifrado               TEXT,
    cpf_indice                CHAR(64),

    -- Pessoa jurídica (CNPJ é público, por isso não é cifrado)
    razao_social              VARCHAR(150),
    cnpj                      VARCHAR(14),

    telefone_cifrado          TEXT,

    email_verificado          BOOLEAN      NOT NULL DEFAULT false,
    token_ativacao_hash       CHAR(64),
    token_ativacao_expira     TIMESTAMPTZ,
    token_recuperacao_hash    CHAR(64),
    token_recuperacao_expira  TIMESTAMPTZ,

    tentativas_login_falhas   INTEGER      NOT NULL DEFAULT 0,
    bloqueado_ate             TIMESTAMPTZ,
    ultimo_login_em           TIMESTAMPTZ,
    criado_em                 TIMESTAMPTZ  NOT NULL DEFAULT now(),
    atualizado_em             TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT clientes_tipo_valido CHECK (tipo_cliente IN ('PF', 'PJ')),
    CONSTRAINT clientes_dados_por_tipo CHECK (
        (tipo_cliente = 'PF' AND nome IS NOT NULL AND cpf_indice IS NOT NULL)
     OR (tipo_cliente = 'PJ' AND razao_social IS NOT NULL AND cnpj IS NOT NULL)
    ),
    CONSTRAINT clientes_tentativas_validas CHECK (tentativas_login_falhas >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS clientes_email_unico        ON clientes (lower(email));
CREATE UNIQUE INDEX IF NOT EXISTS clientes_cpf_indice_unico   ON clientes (cpf_indice);
CREATE UNIQUE INDEX IF NOT EXISTS clientes_cnpj_unico         ON clientes (cnpj);
CREATE UNIQUE INDEX IF NOT EXISTS clientes_token_ativacao_unico    ON clientes (token_ativacao_hash)    WHERE token_ativacao_hash IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS clientes_token_recuperacao_unico ON clientes (token_recuperacao_hash) WHERE token_recuperacao_hash IS NOT NULL;

CREATE OR REPLACE FUNCTION definir_atualizado_em() RETURNS trigger AS $$
BEGIN
    NEW.atualizado_em := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS clientes_atualizado_em ON clientes;
CREATE TRIGGER clientes_atualizado_em
    BEFORE UPDATE ON clientes
    FOR EACH ROW EXECUTE FUNCTION definir_atualizado_em();

COMMIT;

-- Conferir: deve mostrar 0 clientes e as colunas novas (senha_hash, cpf_cifrado…)
SELECT count(*) AS total_clientes FROM clientes;

-- =============================================================================
-- Vinheria Agnello — esquema completo do banco de dados (PostgreSQL 14+)
-- =============================================================================
-- Use este arquivo para criar um banco NOVO do zero (ex.: um ambiente de testes).
-- Para atualizar o banco que já existe no Neon, use os scripts em db/migracoes/
-- (o resultado final é o mesmo).
--
-- Proteção dos dados pessoais:
--   senha_hash        PBKDF2-HMAC-SHA256, 600 000 iterações, salt por senha
--   cpf_cifrado       AES-256-GCM (chave fora do banco: AGNELLO_CHAVE_DADOS)
--   cpf_indice        HMAC-SHA256 do CPF (chave: AGNELLO_CHAVE_INDICE) → unicidade/busca
--   telefone_cifrado  AES-256-GCM
--   token_*_hash      SHA-256 do token enviado por e-mail (o token em si não é guardado)
-- =============================================================================

BEGIN;

CREATE TABLE IF NOT EXISTS clientes (
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

CREATE TABLE IF NOT EXISTS produtos (
    id            SERIAL         PRIMARY KEY,
    nome          VARCHAR(150)   NOT NULL,
    tipo          VARCHAR(50)    NOT NULL,
    origem        VARCHAR(80),
    descricao     TEXT,
    preco         NUMERIC(10, 2) NOT NULL,
    preco_antigo  NUMERIC(10, 2),
    desconto      VARCHAR(20),
    pontuacao     VARCHAR(80),
    estoque       INTEGER        NOT NULL DEFAULT 0,
    imagem        VARCHAR(255),

    CONSTRAINT produtos_preco_positivo      CHECK (preco > 0),
    CONSTRAINT produtos_preco_antigo_valido CHECK (preco_antigo IS NULL OR preco_antigo >= 0),
    CONSTRAINT produtos_estoque_valido      CHECK (estoque >= 0)
);

COMMIT;

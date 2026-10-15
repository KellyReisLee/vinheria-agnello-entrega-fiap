-- =============================================================================
-- V3 — Remove os dados em texto puro e reforça as regras da tabela "clientes"
-- =============================================================================
-- Etapa 3 de 3. Só execute DEPOIS de:
--   1) rodar o V2;
--   2) rodar o programa com.agnello.migracao.MigrarDadosSensiveis;
--   3) publicar a versão nova da aplicação e rodar o programa de novo
--      (pega clientes que se cadastraram pela versão antiga entretanto).
--
-- Se ainda houver algum cliente por migrar, o script PARA sem alterar nada.
-- ATENÇÃO: depois deste script, as colunas antigas deixam de existir.
--          Faça um backup antes (Neon → Branches → Create branch).
-- =============================================================================

BEGIN;

-- 1. Verificação de segurança: nada pode ficar para trás ----------------------
DO $$
DECLARE
    sem_senha    INTEGER;
    cpf_pendente INTEGER := 0;
    tel_pendente INTEGER := 0;
BEGIN
    SELECT count(*) INTO sem_senha FROM clientes WHERE senha_hash IS NULL;

    -- As colunas antigas só existem antes da 1.ª execução deste script
    -- (consulta dinâmica para o script poder ser executado de novo sem erro)
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'clientes' AND column_name = 'cpf') THEN
        EXECUTE 'SELECT count(*) FROM clientes WHERE nullif(trim(cpf), '''') IS NOT NULL AND cpf_cifrado IS NULL'
            INTO cpf_pendente;
        EXECUTE 'SELECT count(*) FROM clientes WHERE nullif(trim(telefone), '''') IS NOT NULL AND telefone_cifrado IS NULL'
            INTO tel_pendente;
    END IF;

    IF sem_senha + cpf_pendente + tel_pendente > 0 THEN
        RAISE EXCEPTION 'Migração incompleta: % sem senha_hash, % CPF e % telefone por cifrar. Rode MigrarDadosSensiveis primeiro.',
            sem_senha, cpf_pendente, tel_pendente;
    END IF;
END $$;

-- 2. Apaga as colunas em texto puro -------------------------------------------
ALTER TABLE clientes
    DROP COLUMN IF EXISTS senha,
    DROP COLUMN IF EXISTS cpf,
    DROP COLUMN IF EXISTS telefone,
    DROP COLUMN IF EXISTS token_ativacao,
    DROP COLUMN IF EXISTS token_recuperacao,
    DROP COLUMN IF EXISTS token_expiracao;

ALTER TABLE clientes ALTER COLUMN senha_hash SET NOT NULL;

-- 3. Regras de integridade ----------------------------------------------------
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_tipo_valido;
ALTER TABLE clientes ADD CONSTRAINT clientes_tipo_valido CHECK (tipo_cliente IN ('PF', 'PJ'));

-- PF precisa de nome e CPF; PJ precisa de razão social e CNPJ.
-- NOT VALID: vale para os registros novos e alterados; os antigos não bloqueiam o script.
-- Para conferir também os antigos, rode depois:
--   ALTER TABLE clientes VALIDATE CONSTRAINT clientes_dados_por_tipo;
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_dados_por_tipo;
ALTER TABLE clientes ADD CONSTRAINT clientes_dados_por_tipo CHECK (
    (tipo_cliente = 'PF' AND nome IS NOT NULL AND cpf_indice IS NOT NULL)
 OR (tipo_cliente = 'PJ' AND razao_social IS NOT NULL AND cnpj IS NOT NULL)
) NOT VALID;

ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_tentativas_validas;
ALTER TABLE clientes ADD CONSTRAINT clientes_tentativas_validas CHECK (tentativas_login_falhas >= 0);

-- 4. Índices únicos -----------------------------------------------------------
-- E-mail único sem diferenciar maiúsculas (Ana@x.com = ana@x.com)
CREATE UNIQUE INDEX IF NOT EXISTS clientes_email_unico        ON clientes (lower(email));
CREATE UNIQUE INDEX IF NOT EXISTS clientes_cpf_indice_unico   ON clientes (cpf_indice);
CREATE UNIQUE INDEX IF NOT EXISTS clientes_cnpj_unico         ON clientes (cnpj);
CREATE UNIQUE INDEX IF NOT EXISTS clientes_token_ativacao_unico    ON clientes (token_ativacao_hash)    WHERE token_ativacao_hash IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS clientes_token_recuperacao_unico ON clientes (token_recuperacao_hash) WHERE token_recuperacao_hash IS NOT NULL;

-- 5. atualizado_em preenchido automaticamente em cada UPDATE ------------------
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

-- =============================================================================
-- V7 — Conta do cliente (endereços, favoritos, cancelamento) e busca do catálogo
-- =============================================================================
-- Rode no Neon (SQL Editor) ANTES de publicar esta versão. Pode rodar mais de
-- uma vez: tudo usa IF NOT EXISTS.
-- =============================================================================

BEGIN;

-- Endereços guardados pelo cliente. O endereço inteiro fica cifrado
-- (AES-256-GCM, mesma chave do CPF); só o apelido ("Casa") fica legível.
CREATE TABLE IF NOT EXISTS enderecos (
    id          SERIAL       PRIMARY KEY,
    cliente_id  INTEGER      NOT NULL REFERENCES clientes (id) ON DELETE CASCADE,
    apelido     VARCHAR(40)  NOT NULL,
    dados_cifrados TEXT      NOT NULL,
    principal   BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS enderecos_cliente ON enderecos (cliente_id);
-- No máximo um endereço principal por cliente
CREATE UNIQUE INDEX IF NOT EXISTS enderecos_um_principal ON enderecos (cliente_id) WHERE principal;

-- Vinhos favoritos (a chave primária impede o mesmo vinho duas vezes)
CREATE TABLE IF NOT EXISTS favoritos (
    cliente_id  INTEGER      NOT NULL REFERENCES clientes (id) ON DELETE CASCADE,
    produto_id  INTEGER      NOT NULL REFERENCES produtos (id) ON DELETE CASCADE,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY (cliente_id, produto_id)
);

-- Histórico: distingue mudanças feitas pelo próprio cliente (cancelamento)
ALTER TABLE historico_pedidos ADD COLUMN IF NOT EXISTS pelo_cliente BOOLEAN NOT NULL DEFAULT FALSE;

-- Índices para a busca e os filtros do catálogo
CREATE INDEX IF NOT EXISTS produtos_ativos_preco ON produtos (preco) WHERE ativo;
CREATE INDEX IF NOT EXISTS produtos_ativos_tipo  ON produtos (lower(tipo)) WHERE ativo;

COMMIT;

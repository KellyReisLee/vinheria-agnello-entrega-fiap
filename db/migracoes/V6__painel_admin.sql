-- =============================================================================
-- V6 — Painel administrativo
-- =============================================================================
-- Rode no Neon (SQL Editor) ANTES de publicar a versão do site com o /admin.
-- Pode rodar mais de uma vez: tudo usa IF NOT EXISTS.
--
-- - administradores: contas da equipe da loja, separadas dos clientes. Um
--   cliente nunca vira administrador por engano, e cada tabela tem as suas regras.
-- - produtos.ativo: vinhos são DESATIVADOS, não apagados. Os pedidos antigos
--   continuam a apontar para eles e o vinho pode voltar à loja depois.
-- - imagens_produto: as fotos enviadas pelo painel ficam no banco. O disco do
--   Render é apagado a cada deploy, por isso um arquivo salvo numa pasta sumiria.
-- - mensagens_contato: passa a guardar a resposta enviada ao cliente.
-- =============================================================================

BEGIN;

CREATE TABLE IF NOT EXISTS administradores (
    id                       SERIAL        PRIMARY KEY,
    nome                     VARCHAR(100)  NOT NULL,
    email                    VARCHAR(254)  NOT NULL,
    senha_hash               VARCHAR(255)  NOT NULL,
    ativo                    BOOLEAN       NOT NULL DEFAULT TRUE,
    tentativas_login_falhas  INTEGER       NOT NULL DEFAULT 0,
    bloqueado_ate            TIMESTAMPTZ,
    ultimo_login_em          TIMESTAMPTZ,
    criado_em                TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS administradores_email_unico ON administradores (lower(email));

-- ----------------------------------------------------------------- produtos
ALTER TABLE produtos ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE produtos ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now();

CREATE TABLE IF NOT EXISTS imagens_produto (
    id            SERIAL       PRIMARY KEY,
    tipo_conteudo VARCHAR(30)  NOT NULL,
    dados         BYTEA        NOT NULL,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT imagens_tipo_valido    CHECK (tipo_conteudo IN ('image/png', 'image/jpeg', 'image/webp')),
    CONSTRAINT imagens_tamanho_maximo CHECK (octet_length(dados) <= 2097152)  -- 2 MB
);

-- ----------------------------------------------------------------- contato
ALTER TABLE mensagens_contato ADD COLUMN IF NOT EXISTS resposta       TEXT;
ALTER TABLE mensagens_contato ADD COLUMN IF NOT EXISTS respondida_em  TIMESTAMPTZ;
ALTER TABLE mensagens_contato ADD COLUMN IF NOT EXISTS respondida_por INTEGER
    REFERENCES administradores (id) ON DELETE SET NULL;

-- ----------------------------------------------------------------- pedidos
-- Histórico de mudanças de status: quem mudou, de quê para quê e quando.
CREATE TABLE IF NOT EXISTS historico_pedidos (
    id               SERIAL       PRIMARY KEY,
    pedido_id        INTEGER      NOT NULL REFERENCES pedidos (id) ON DELETE CASCADE,
    status_anterior  VARCHAR(30)  NOT NULL,
    status_novo      VARCHAR(30)  NOT NULL,
    administrador_id INTEGER      REFERENCES administradores (id) ON DELETE SET NULL,
    criado_em        TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS historico_pedidos_pedido ON historico_pedidos (pedido_id, criado_em);
CREATE INDEX IF NOT EXISTS pedidos_status_data ON pedidos (status, criado_em DESC);

COMMIT;

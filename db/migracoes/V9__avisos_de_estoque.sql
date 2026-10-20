-- =============================================================================
-- V9 — "Avise-me quando chegar" (vinhos esgotados)
-- =============================================================================
-- Rode no Neon (SQL Editor) ANTES de publicar esta versão. Pode rodar mais de
-- uma vez: tudo usa IF NOT EXISTS.
-- =============================================================================

BEGIN;

-- Quem pediu para ser avisado quando um vinho esgotado voltar ao estoque.
-- O e-mail fica guardado só até o aviso ser enviado (avisado_em).
CREATE TABLE IF NOT EXISTS avisos_estoque (
    id          SERIAL        PRIMARY KEY,
    produto_id  INTEGER       NOT NULL REFERENCES produtos (id) ON DELETE CASCADE,
    email       VARCHAR(254)  NOT NULL,
    cliente_id  INTEGER       REFERENCES clientes (id) ON DELETE CASCADE, -- conta excluída: some junto
    criado_em   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    avisado_em  TIMESTAMPTZ
);

-- Um pedido de aviso por pessoa e vinho (enquanto não for avisada)
CREATE UNIQUE INDEX IF NOT EXISTS avisos_estoque_unico
    ON avisos_estoque (produto_id, email) WHERE avisado_em IS NULL;

COMMIT;

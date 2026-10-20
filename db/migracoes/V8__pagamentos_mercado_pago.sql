-- =============================================================================
-- V8 — Pagamento real com o Mercado Pago
-- =============================================================================
-- Rode no Neon (SQL Editor) ANTES de publicar esta versão. Pode rodar mais de
-- uma vez: tudo usa IF NOT EXISTS.
-- =============================================================================

BEGIN;

-- Dados do pagamento no Mercado Pago (nunca guardamos dados do cartão)
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS pagamento_id     VARCHAR(40);
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS pagamento_status VARCHAR(40);
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS pago_em          TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS pedidos_pagamento_id ON pedidos (pagamento_id) WHERE pagamento_id IS NOT NULL;

-- Histórico: distingue a aprovação automática feita pelo Mercado Pago
ALTER TABLE historico_pedidos ADD COLUMN IF NOT EXISTS pelo_gateway BOOLEAN NOT NULL DEFAULT FALSE;

COMMIT;

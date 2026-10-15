-- =============================================================================
-- V4 — Regras de integridade da tabela "produtos" (opcional, recomendado)
-- =============================================================================
-- - Preços em NUMERIC(10,2): valores em dinheiro nunca devem usar float/double,
--   que têm erros de arredondamento (0.1 + 0.2 = 0.30000000000000004).
-- - Proíbe preço negativo e estoque negativo.
-- Se algum produto atual violar uma regra, o script para e nada é alterado;
-- corrija o produto e rode de novo.
-- =============================================================================

BEGIN;

ALTER TABLE produtos ALTER COLUMN preco        TYPE NUMERIC(10, 2) USING round(preco::numeric, 2);
ALTER TABLE produtos ALTER COLUMN preco_antigo TYPE NUMERIC(10, 2) USING round(preco_antigo::numeric, 2);

ALTER TABLE produtos DROP CONSTRAINT IF EXISTS produtos_preco_positivo;
ALTER TABLE produtos ADD CONSTRAINT produtos_preco_positivo CHECK (preco > 0);

ALTER TABLE produtos DROP CONSTRAINT IF EXISTS produtos_preco_antigo_valido;
ALTER TABLE produtos ADD CONSTRAINT produtos_preco_antigo_valido CHECK (preco_antigo IS NULL OR preco_antigo >= 0);

ALTER TABLE produtos DROP CONSTRAINT IF EXISTS produtos_estoque_valido;
ALTER TABLE produtos ADD CONSTRAINT produtos_estoque_valido CHECK (estoque >= 0);

COMMIT;

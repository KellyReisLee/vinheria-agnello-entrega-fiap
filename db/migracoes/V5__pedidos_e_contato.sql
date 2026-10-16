-- =============================================================================
-- V5 — Pedidos e mensagens de contato
-- =============================================================================
-- Rode no Neon (SQL Editor) ANTES de publicar a versão do site que grava pedidos.
-- Pode rodar mais de uma vez: tudo usa IF NOT EXISTS.
--
-- Decisões importantes:
-- - Dinheiro em NUMERIC(10,2), nunca float.
-- - O item guarda o NOME e o PREÇO do vinho no momento da compra ("foto" do
--   pedido): se o preço mudar amanhã, o pedido antigo continua com o valor pago.
-- - Se o cliente excluir a conta, os pedidos ficam (a lei fiscal obriga a
--   guardar), mas sem ligação à pessoa: cliente_id passa a NULL.
-- - O endereço de entrega é dado pessoal: fica cifrado (AES-256-GCM) pela
--   aplicação, como o CPF e o telefone.
-- - Nunca guardamos dados de cartão (número, validade, CVV).
-- =============================================================================

BEGIN;

CREATE TABLE IF NOT EXISTS pedidos (
    id                SERIAL         PRIMARY KEY,
    -- Número mostrado ao cliente: AG-000123 (gerado pelo próprio banco a partir do id)
    numero            VARCHAR(12)    GENERATED ALWAYS AS ('AG-' || lpad(id::text, 6, '0')) STORED,
    cliente_id        INTEGER        REFERENCES clientes (id) ON DELETE SET NULL,
    status            VARCHAR(30)    NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    forma_pagamento   VARCHAR(10)    NOT NULL,
    parcelas          SMALLINT       NOT NULL DEFAULT 1,
    tipo_entrega      VARCHAR(10)    NOT NULL,
    subtotal          NUMERIC(10, 2) NOT NULL,
    frete             NUMERIC(10, 2) NOT NULL DEFAULT 0,
    desconto          NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total             NUMERIC(10, 2) NOT NULL,
    endereco_cifrado  TEXT,
    criado_em         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    atualizado_em     TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT pedidos_status_valido CHECK (status IN
        ('AGUARDANDO_PAGAMENTO', 'PAGO', 'EM_SEPARACAO', 'ENVIADO', 'PRONTO_RETIRADA', 'ENTREGUE', 'CANCELADO')),
    CONSTRAINT pedidos_pagamento_valido CHECK (forma_pagamento IN ('PIX', 'CARTAO', 'BOLETO')),
    CONSTRAINT pedidos_parcelas_validas CHECK (parcelas BETWEEN 1 AND 6
                                               AND (forma_pagamento = 'CARTAO' OR parcelas = 1)),
    CONSTRAINT pedidos_entrega_valida   CHECK (tipo_entrega IN ('ENTREGA', 'RETIRADA')),
    CONSTRAINT pedidos_endereco_entrega CHECK (tipo_entrega = 'RETIRADA' OR endereco_cifrado IS NOT NULL),
    CONSTRAINT pedidos_valores_validos  CHECK (subtotal > 0 AND frete >= 0 AND desconto >= 0
                                               AND total = subtotal + frete - desconto)
);

CREATE UNIQUE INDEX IF NOT EXISTS pedidos_numero_unico ON pedidos (numero);
-- "Meus pedidos": busca por cliente, do mais recente para o mais antigo
CREATE INDEX IF NOT EXISTS pedidos_cliente_data ON pedidos (cliente_id, criado_em DESC);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id              SERIAL         PRIMARY KEY,
    pedido_id       INTEGER        NOT NULL REFERENCES pedidos (id) ON DELETE CASCADE,
    produto_id      INTEGER        REFERENCES produtos (id) ON DELETE SET NULL,
    nome_produto    VARCHAR(150)   NOT NULL,
    imagem          VARCHAR(255),
    preco_unitario  NUMERIC(10, 2) NOT NULL,
    quantidade      INTEGER        NOT NULL,

    CONSTRAINT itens_preco_positivo     CHECK (preco_unitario > 0),
    CONSTRAINT itens_quantidade_valida  CHECK (quantidade BETWEEN 1 AND 99)
);

CREATE INDEX IF NOT EXISTS itens_pedido_pedido ON itens_pedido (pedido_id);

CREATE TABLE IF NOT EXISTS mensagens_contato (
    id          SERIAL        PRIMARY KEY,
    cliente_id  INTEGER       REFERENCES clientes (id) ON DELETE SET NULL,
    nome        VARCHAR(120)  NOT NULL,
    email       VARCHAR(254)  NOT NULL,
    assunto     VARCHAR(40)   NOT NULL,
    numero_pedido VARCHAR(20),
    mensagem    TEXT          NOT NULL,
    respondida  BOOLEAN       NOT NULL DEFAULT FALSE,
    criado_em   TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT contato_mensagem_tamanho CHECK (char_length(mensagem) BETWEEN 10 AND 2000)
);

CREATE INDEX IF NOT EXISTS mensagens_contato_pendentes ON mensagens_contato (criado_em DESC) WHERE NOT respondida;

-- Atualiza "atualizado_em" sozinho quando o status do pedido muda.
-- (a função definir_atualizado_em já existe desde a V2; recriamos por garantia)
CREATE OR REPLACE FUNCTION definir_atualizado_em() RETURNS trigger AS $$
BEGIN
    NEW.atualizado_em := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS pedidos_atualizado_em ON pedidos;
CREATE TRIGGER pedidos_atualizado_em
    BEFORE UPDATE ON pedidos
    FOR EACH ROW EXECUTE FUNCTION definir_atualizado_em();

COMMIT;

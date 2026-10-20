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
--   endereco_cifrado  (pedidos) AES-256-GCM
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

-- ----------------------------------------------------------------- pedidos e contato (V5)
--   endereco_cifrado  AES-256-GCM (mesma chave do CPF)

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

DROP TRIGGER IF EXISTS pedidos_atualizado_em ON pedidos;
CREATE TRIGGER pedidos_atualizado_em
    BEFORE UPDATE ON pedidos
    FOR EACH ROW EXECUTE FUNCTION definir_atualizado_em();

-- ----------------------------------------------------------------- painel administrativo (V6)

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

-- ----------------------------------------------------------------- conta do cliente e busca (V7)

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

-- ----------------------------------------------------------------- pagamentos Mercado Pago (V8)

-- Dados do pagamento no Mercado Pago (nunca guardamos dados do cartão)
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS pagamento_id     VARCHAR(40);
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS pagamento_status VARCHAR(40);
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS pago_em          TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS pedidos_pagamento_id ON pedidos (pagamento_id) WHERE pagamento_id IS NOT NULL;

-- Histórico: distingue a aprovação automática feita pelo Mercado Pago
ALTER TABLE historico_pedidos ADD COLUMN IF NOT EXISTS pelo_gateway BOOLEAN NOT NULL DEFAULT FALSE;

COMMIT;

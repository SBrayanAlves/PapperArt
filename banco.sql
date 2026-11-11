-- =====================================================================
-- PapperArt: criação do banco
-- Rode este arquivo dentro de um banco vazio no PostgreSQL (ex: papperart).
-- Pode rodar de novo quando quiser: ele apaga as tabelas e cria do zero.
-- =====================================================================

DROP TABLE IF EXISTS pagamento;
DROP TABLE IF EXISTS item_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS funcionario;
DROP TABLE IF EXISTS cliente;

-- ---------------------------------------------------------------------
-- Cliente: entra na área de compras
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id               SERIAL       PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,                         -- login (RN02)
    senha_hash       VARCHAR(255) NOT NULL,                                -- só o hash (RN13)
    cpf              CHAR(11)     NOT NULL UNIQUE CHECK (cpf ~ '^[0-9]{11}$'),       -- RN11
    data_nascimento  DATE         NOT NULL,                                -- RN12
    telefone         VARCHAR(11)  NOT NULL CHECK (telefone ~ '^[0-9]{10,11}$')     -- RN02
);

-- ---------------------------------------------------------------------
-- Funcionário: entra na área de gestão.
-- Tabela separada do cliente: uma conta de cliente nunca vira de funcionário.
-- ---------------------------------------------------------------------
CREATE TABLE funcionario (
    id          SERIAL       PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,  -- login
    senha_hash  VARCHAR(255) NOT NULL,         -- só o hash (RN13)
    cargo       VARCHAR(50)  NOT NULL,
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE  -- RN14: desativado não entra
);

-- ---------------------------------------------------------------------
-- Produto
-- ---------------------------------------------------------------------
CREATE TABLE produto (
    id         SERIAL        PRIMARY KEY,
    nome       VARCHAR(100)  NOT NULL,
    descricao  TEXT,
    preco      NUMERIC(10,2) NOT NULL CHECK (preco > 0),     -- RN01
    estoque    INTEGER       NOT NULL CHECK (estoque >= 0),  -- RN01
    categoria  VARCHAR(20)   NOT NULL
               CHECK (categoria IN ('ESCOLAR', 'ESCRITORIO', 'ARTE', 'PRESENTES', 'OUTROS'))
);

-- ---------------------------------------------------------------------
-- Pedido (o pedido ABERTO é o carrinho do cliente)
-- ---------------------------------------------------------------------
CREATE TABLE pedido (
    id          SERIAL      PRIMARY KEY,
    cliente_id  INTEGER     NOT NULL REFERENCES cliente (id),
    data        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      VARCHAR(10) NOT NULL DEFAULT 'ABERTO'
                CHECK (status IN ('ABERTO', 'PAGO', 'ENTREGUE', 'CANCELADO'))
);

-- RN03: cada cliente tem no máximo 1 pedido ABERTO (o carrinho)
CREATE UNIQUE INDEX um_pedido_aberto_por_cliente
    ON pedido (cliente_id)
    WHERE status = 'ABERTO';

-- ---------------------------------------------------------------------
-- Item do pedido (composição: apagou o pedido, os itens vão junto)
-- ---------------------------------------------------------------------
CREATE TABLE item_pedido (
    id              SERIAL        PRIMARY KEY,
    pedido_id       INTEGER       NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
    produto_id      INTEGER       NOT NULL REFERENCES produto (id),
    quantidade      INTEGER       NOT NULL CHECK (quantidade > 0),
    preco_unitario  NUMERIC(10,2) NOT NULL CHECK (preco_unitario > 0),  -- RN05: preço congelado
    UNIQUE (pedido_id, produto_id)  -- o mesmo produto não aparece duas vezes no pedido
);

-- ---------------------------------------------------------------------
-- Pagamento: os 3 tipos (herança) numa tabela só, com a coluna "tipo"
-- ---------------------------------------------------------------------
CREATE TABLE pagamento (
    id              SERIAL        PRIMARY KEY,
    pedido_id       INTEGER       NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
    tipo            VARCHAR(10)   NOT NULL CHECK (tipo IN ('PIX', 'CARTAO', 'DINHEIRO')),
    valor           NUMERIC(10,2) NOT NULL CHECK (valor > 0),
    data            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_cartao     VARCHAR(10)   CHECK (tipo_cartao IN ('CREDITO', 'DEBITO')),  -- só cartão
    parcelas        INTEGER       CHECK (parcelas >= 1),                         -- só cartão
    valor_recebido  NUMERIC(10,2),                                               -- só dinheiro

    -- Cada tipo preenche as colunas dele
    CHECK (tipo <> 'CARTAO'   OR (tipo_cartao IS NOT NULL AND parcelas IS NOT NULL)),
    CHECK (tipo <> 'DINHEIRO' OR (valor_recebido IS NOT NULL AND valor_recebido >= valor)),  -- RN09
    CHECK (tipo_cartao <> 'DEBITO' OR parcelas = 1)                                         -- RN08
);

-- =====================================================================
-- Dados de exemplo
-- Contas de demonstração, só pro banco local:
--   clientes:     senha Cliente123
--   funcionários: senha Admin1234
-- CPFs fictícios, válidos só no formato.
-- =====================================================================
INSERT INTO cliente (nome, email, senha_hash, cpf, data_nascimento, telefone) VALUES
    ('Ana Souza',    'ana.souza@email.com',
     '600000:1b3b8RIuCBCPwCQbrJllJg==:drYT25RPSVruys8r1/VEdBQZlCpVzQ2mc+j6dtY1J50=',
     '52998224725', '2000-05-10', '61999990001'),
    ('Bruno Lima',   'bruno.lima@email.com',
     '600000:jjZZym/prCtwWcGRmgMr7A==:TNZ1JxJy6+NFQKg0YNhdMwtpGJE2aZFar8OSklfTwy0=',
     '12345678909', '1995-11-23', '61999990002'),
    ('Carla Mendes', 'carla.mendes@email.com',
     '600000:+9GruT25vcMTkXZXkRFK0g==:dwj/yGj2OtNKikq4JrJVooSU5eEPtb2GnLADhhBEIxQ=',
     '11144477735', '2008-02-14', '61999990003');

INSERT INTO funcionario (nome, email, senha_hash, cargo, ativo) VALUES
    ('Administradora', 'admin@papperart.com',
     '600000:7o3fbOeGGtKq5FPcnG5VxQ==:SIwEX6v9ciXXVp0hrdllavdb2fmArADZqJcOjiF9Rsw=',
     'Gerente', TRUE),
    ('Ex-atendente',   'atendente@papperart.com',
     '600000:j9NarVPfZ9Gwt2sIIJU/Fg==:HRW4cTAnNlmBxOf7R2Shi0zakzkkOssKtMu0iCsl+LA=',
     'Atendente', FALSE);  -- desativada: não consegue entrar (RN14)

INSERT INTO produto (nome, descricao, preco, estoque, categoria) VALUES
    ('Caderno universitário 10 matérias', '200 folhas, capa dura',  32.90,  20, 'ESCOLAR'),
    ('Caneta esferográfica azul',         'Ponta média 1.0 mm',      2.50, 100, 'ESCOLAR'),
    ('Lapiseira 0.7 mm',                  'Com borracha',            9.90,  30, 'ESCOLAR'),
    ('Bloco de folhas A4',                'Pacote com 100 folhas',  18.00,  15, 'ESCRITORIO'),
    ('Kit de canetas brush',              '12 cores',               45.00,   8, 'ARTE'),
    ('Caneca de porcelana',               '325 ml',                 39.90,   5, 'PRESENTES');

-- Pedido 1: a Ana comprou uma lapiseira e pagou no Pix
INSERT INTO pedido (cliente_id, status) VALUES (1, 'PAGO');
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (1, 3, 1, 9.90);
INSERT INTO pagamento (pedido_id, tipo, valor) VALUES (1, 'PIX', 9.90);

-- Pedido 2: a Ana separou folhas pra pagar quando voltar (ABERTO = carrinho)
INSERT INTO pedido (cliente_id, status) VALUES (1, 'ABERTO');
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (2, 4, 2, 18.00);

-- Pedido 3: o Bruno pagou parte em dinheiro e parte no cartão (total R$ 42,90)
INSERT INTO pedido (cliente_id, status) VALUES (2, 'PAGO');
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES
    (3, 1, 1, 32.90),
    (3, 2, 4, 2.50);
INSERT INTO pagamento (pedido_id, tipo, valor, valor_recebido) VALUES (3, 'DINHEIRO', 20.00, 20.00);
INSERT INTO pagamento (pedido_id, tipo, valor, tipo_cartao, parcelas) VALUES (3, 'CARTAO', 22.90, 'CREDITO', 1);

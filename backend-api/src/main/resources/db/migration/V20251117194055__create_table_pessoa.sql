CREATE TABLE pessoa (
     id BIGSERIAL PRIMARY KEY,
     nome VARCHAR(255) NOT NULL,
     telefone VARCHAR(20),
     cpfcnpj VARCHAR(14) UNIQUE,
     logradouro VARCHAR(255),
     numero VARCHAR(20),
     bairro VARCHAR(100),
     cidade VARCHAR(100),
     uf CHAR(2),
     cep VARCHAR(10)
);

CREATE INDEX idx_pessoas_nome ON pessoa(nome);
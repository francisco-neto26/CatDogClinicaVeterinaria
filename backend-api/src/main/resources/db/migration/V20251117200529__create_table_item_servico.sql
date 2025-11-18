CREATE TABLE item_servico (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL UNIQUE,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    tipo_item_id BIGINT NOT NULL,
    CONSTRAINT fk_item_servico_tipo
      FOREIGN KEY (tipo_item_id)
          REFERENCES tipo_item(id)
);
CREATE TABLE conta_item (
    id BIGSERIAL PRIMARY KEY,
    conta_id BIGINT NOT NULL,
    item_servico_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario_momento DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_conta_item_conta
        FOREIGN KEY (conta_id)
            REFERENCES conta(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_conta_item_servico
        FOREIGN KEY (item_servico_id)
            REFERENCES item_servico(id)
);
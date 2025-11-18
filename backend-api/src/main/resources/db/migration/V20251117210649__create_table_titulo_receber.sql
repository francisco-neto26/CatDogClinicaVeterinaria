CREATE TABLE titulo_receber (
    id BIGSERIAL PRIMARY KEY,
    valor DECIMAL(10, 2) NOT NULL,
    data_emissao DATE NOT NULL DEFAULT CURRENT_DATE,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    conta_id BIGINT NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN (
                                                  'PENDENTE',
                                                  'PAGO',
                                                  'VENCIDO',
                                                  'CANCELADO'
        )),
    CONSTRAINT fk_titulo_conta
        FOREIGN KEY (conta_id)
            REFERENCES conta(id),
    CONSTRAINT fk_titulo_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario(id)
);
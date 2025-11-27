CREATE TABLE conta (
    id BIGSERIAL PRIMARY KEY,
    data_emissao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDENTE', 'PAGO', 'CANCELADO')),
    usuario_id BIGINT NOT NULL,
    agendamento_id BIGINT NOT NULL UNIQUE,

    CONSTRAINT fk_conta_usuario
       FOREIGN KEY (usuario_id)
           REFERENCES usuario(id),

    CONSTRAINT fk_conta_agendamento
       FOREIGN KEY (agendamento_id)
           REFERENCES agendamentos(id)
);
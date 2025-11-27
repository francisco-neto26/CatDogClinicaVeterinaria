ALTER TABLE agendamentos
    ADD COLUMN funcionario_id BIGINT;

ALTER TABLE agendamentos
    ADD CONSTRAINT fk_agendamento_funcionario
        FOREIGN KEY (funcionario_id)
            REFERENCES usuario(id);
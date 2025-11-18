CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    descricao TEXT,
    usuario_id BIGINT NOT NULL,
    animal_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN (
                                                'AGENDADO',
                                                'CONCLUÍDO',
                                                'CANCELADO'
      )),
    CONSTRAINT fk_agendamento_usuario
      FOREIGN KEY (usuario_id)
          REFERENCES usuario(id),
    CONSTRAINT fk_agendamento_animal
      FOREIGN KEY (animal_id)
          REFERENCES animal(id)
);
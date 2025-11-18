CREATE TABLE animal (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    raca VARCHAR(100),
    data_nascimento DATE,
    especie VARCHAR(50) NOT NULL,
    sexo VARCHAR(10) NOT NULL CHECK (sexo IN ('MACHO', 'FÊMEA')),
    cor_pelagem VARCHAR(50),
    foto_url VARCHAR(255),
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_animal_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario(id)
);
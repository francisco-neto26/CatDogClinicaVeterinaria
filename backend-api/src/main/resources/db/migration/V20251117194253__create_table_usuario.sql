CREATE TABLE usuario (
      id BIGSERIAL PRIMARY KEY,
      email VARCHAR(255) NOT NULL UNIQUE,
      senha VARCHAR(255) NOT NULL,
      pessoa_id BIGINT NOT NULL,
      role_id BIGINT NOT NULL,
      CONSTRAINT fk_usuarios_pessoa
          FOREIGN KEY (pessoa_id)
              REFERENCES pessoa(id)
              ON DELETE CASCADE,
      CONSTRAINT fk_usuarios_role
          FOREIGN KEY (role_id)
              REFERENCES roles(id)
);
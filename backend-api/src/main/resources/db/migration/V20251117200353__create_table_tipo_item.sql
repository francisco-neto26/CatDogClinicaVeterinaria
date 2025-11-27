CREATE TABLE tipo_item (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);
/*Atualmente fixo, mas vamos em outro momento implementar cadastro*/
INSERT INTO tipo_item (nome) VALUES ('CONSULTA');
INSERT INTO tipo_item (nome) VALUES ('PROCEDIMENTO');
INSERT INTO tipo_item (nome) VALUES ('MEDICAMENTO');
INSERT INTO tipo_item (nome) VALUES ('EXAME');
INSERT INTO tipo_item (nome) VALUES ('OUTROS');
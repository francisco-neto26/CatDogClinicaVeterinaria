ALTER TABLE animal
    ADD CONSTRAINT animal_sexo_check
        CHECK (sexo IN ('MACHO', 'FEMEA'));
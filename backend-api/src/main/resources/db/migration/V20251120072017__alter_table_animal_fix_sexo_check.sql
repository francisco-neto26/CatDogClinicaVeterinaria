ALTER TABLE animal
    DROP CONSTRAINT IF EXISTS animal_sexo_check;

ALTER TABLE animal
    ADD CONSTRAINT animal_sexo_check
        CHECK (sexo IN ('MACHO', 'FEMEA'));
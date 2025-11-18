ALTER TABLE conta
    DROP CONSTRAINT IF EXISTS conta_status_check,
    DROP CONSTRAINT IF EXISTS conta_status_check1;

ALTER TABLE conta
    ADD CONSTRAINT conta_status_operacional_check
        CHECK (status IN ('ABERTA', 'FECHADA', 'CANCELADA'));
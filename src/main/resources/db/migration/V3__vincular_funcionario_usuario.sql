ALTER TABLE funcionarios
    ADD COLUMN usuario_id BIGINT;

ALTER TABLE funcionarios
    ADD CONSTRAINT uk_funcionarios_usuario
        UNIQUE (usuario_id);

CREATE INDEX idx_funcionarios_usuario_id
    ON funcionarios (usuario_id);
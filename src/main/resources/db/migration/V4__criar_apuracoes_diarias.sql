CREATE TABLE apuracoes_diarias (
                                   id BIGSERIAL PRIMARY KEY,
                                   funcionario_id BIGINT NOT NULL,
                                   data DATE NOT NULL,
                                   minutos_previstos INTEGER NOT NULL,
                                   minutos_trabalhados INTEGER NOT NULL,
                                   minutos_deficit INTEGER NOT NULL,
                                   ausente BOOLEAN NOT NULL,
                                   situacao VARCHAR(20) NOT NULL,
                                   criada_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_apuracao_funcionario
                                       FOREIGN KEY (funcionario_id)
                                           REFERENCES funcionarios (id),

                                   CONSTRAINT uk_apuracao_funcionario_data
                                       UNIQUE (funcionario_id, data),

                                   CONSTRAINT ck_apuracao_situacao
                                       CHECK (situacao IN ('REGULAR', 'DEFICIT', 'AUSENTE'))
);
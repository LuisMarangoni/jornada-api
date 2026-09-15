package br.com.luismarangoni.jornada_api.marcacao;

import java.time.Instant;

public class MarcacaoPonto {

    private final Long funcionarioId;
    private final TipoMarcacao tipo;
    private final Instant ocorridaEm;

    public MarcacaoPonto(
            Long funcionarioId,
            TipoMarcacao tipo,
            Instant ocorridaEm
    ) {
        if (funcionarioId == null || funcionarioId <= 0) {
            throw new IllegalArgumentException(
                    "O funcionário deve possuir um ID válido"
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo da marcação é obrigatório"
            );
        }

        if (ocorridaEm == null) {
            throw new IllegalArgumentException(
                    "O horário da marcação é obrigatório"
            );
        }

        this.funcionarioId = funcionarioId;
        this.tipo = tipo;
        this.ocorridaEm = ocorridaEm;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public TipoMarcacao getTipo() {
        return tipo;
    }

    public Instant getOcorridaEm() {
        return ocorridaEm;
    }
}

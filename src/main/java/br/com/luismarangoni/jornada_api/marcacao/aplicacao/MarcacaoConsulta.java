package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;

import java.time.Instant;

public record MarcacaoConsulta(
        Long id,
        Long funcionarioId,
        TipoMarcacao tipo,
        Instant ocorridaEm
) {
}

package br.com.luismarangoni.jornada_api.marcacao.api.dto;

import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;

import java.time.Instant;

public record MarcacaoResponse(
        Long id,
        Long funcionarioId,
        String tipo,
        Instant ocorridaEm
) {
    public static MarcacaoResponse from(MarcacaoConsulta marcacao) {
        return new MarcacaoResponse(
                marcacao.id(),
                marcacao.funcionarioId(),
                marcacao.tipo().name(),
                marcacao.ocorridaEm()
        );
    }
}

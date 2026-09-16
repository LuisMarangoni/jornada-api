package br.com.luismarangoni.jornada_api.marcacao.api.dto;

import br.com.luismarangoni.jornada_api.marcacao.apuracao.ResumoJornada;

public record ResumoJornadaResponse(
        Long funcionarioId,
        long minutosTrabalhados,
        long minutosIntervalo
) {
    public static ResumoJornadaResponse from(
            Long funcionarioId,
            ResumoJornada resumo
    ) {
        return new ResumoJornadaResponse(
                funcionarioId,
                resumo.tempoTrabalhado().toMinutes(),
                resumo.tempoIntervalo().toMinutes()
        );
    }
}

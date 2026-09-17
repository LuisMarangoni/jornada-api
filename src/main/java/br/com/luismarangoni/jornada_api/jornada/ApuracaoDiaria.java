package br.com.luismarangoni.jornada_api.jornada;

import java.time.LocalDate;

public record ApuracaoDiaria(
        Long funcionarioId,
        LocalDate data,
        int minutosPrevistos,
        int minutosTrabalhados,
        int minutosDeficit,
        boolean ausente,
        SituacaoApuracao situacao
) {

    private static final int TOLERANCIA_MINUTOS = 10;

    public static ApuracaoDiaria calcular(
            Long funcionarioId,
            LocalDate data,
            int minutosPrevistos,
            int minutosTrabalhados
    ) {
        boolean ausente = minutosPrevistos > 0 && minutosTrabalhados == 0;

        if (ausente) {
            return new ApuracaoDiaria(
                    funcionarioId, data, minutosPrevistos,
                    minutosTrabalhados, 0, true,
                    SituacaoApuracao.AUSENTE
            );
        }

        int deficitReal = Math.max(0, minutosPrevistos - minutosTrabalhados);
        int deficitDescontavel = Math.max(0, deficitReal - TOLERANCIA_MINUTOS);

        SituacaoApuracao situacao = deficitDescontavel > 0
                ? SituacaoApuracao.DEFICIT
                : SituacaoApuracao.REGULAR;

        return new ApuracaoDiaria(
                funcionarioId, data, minutosPrevistos,
                minutosTrabalhados, deficitDescontavel,
                false, situacao
        );
    }
}

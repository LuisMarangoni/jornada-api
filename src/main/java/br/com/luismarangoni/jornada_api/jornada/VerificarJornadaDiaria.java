package br.com.luismarangoni.jornada_api.jornada;

import br.com.luismarangoni.jornada_api.marcacao.apuracao.ApurarJornadaFuncionario;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class VerificarJornadaDiaria {

    private final ApurarJornadaFuncionario apurador;
    private final JornadaPrevista jornadaPrevista;

    public VerificarJornadaDiaria(
            ApurarJornadaFuncionario apurador,
            JornadaPrevista jornadaPrevista
    ) {
        this.apurador = apurador;
        this.jornadaPrevista = jornadaPrevista;
    }

    public ApuracaoDiaria executar(Long funcionarioId, LocalDate data) {
        var inicio = data.atStartOfDay().toInstant(ZoneOffset.UTC);
        var fim = data.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        var resumo = apurador.executar(funcionarioId, inicio, fim);

        int minutosPrevistos =
                jornadaPrevista.minutosPrevistos(data.getDayOfWeek());

        int minutosTrabalhados =
                (int) resumo.tempoTrabalhado().toMinutes();

        return ApuracaoDiaria.calcular(
                funcionarioId,
                data,
                minutosPrevistos,
                minutosTrabalhados
        );
    }
}

package br.com.luismarangoni.jornada_api.marcacao.apuracao;

import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class ApuradorJornada {

    public ResumoJornada calcular(List<MarcacaoConsulta> marcacoes) {
        Duration tempoTrabalhado = Duration.ZERO;
        Duration tempoIntervalo = Duration.ZERO;

        Instant inicioTrabalho = null;
        Instant inicioIntervalo = null;

        for (MarcacaoConsulta marcacao : marcacoes) {
            Instant horario = marcacao.ocorridaEm();

            switch (marcacao.tipo()) {
                case ENTRADA -> {
                    exigir(inicioTrabalho == null,
                            "Não é permitido iniciar uma jornada já aberta");

                    inicioTrabalho = horario;
                }

                case INICIO_INTERVALO -> {
                    exigir(inicioTrabalho != null,
                            "Não há jornada aberta para iniciar intervalo");

                    exigir(inicioIntervalo == null,
                            "Já existe um intervalo em aberto");

                    inicioIntervalo = horario;
                    tempoTrabalhado = tempoTrabalhado.plus(
                            Duration.between(inicioTrabalho, horario)
                    );
                }

                case FIM_INTERVALO -> {
                    exigir(inicioIntervalo != null,
                            "Não há intervalo em aberto");

                    tempoIntervalo = tempoIntervalo.plus(
                            Duration.between(inicioIntervalo, horario)
                    );

                    inicioIntervalo = null;
                    inicioTrabalho = horario;
                }

                case SAIDA -> {
                    exigir(inicioTrabalho != null,
                            "Não há jornada aberta para registrar saída");

                    exigir(inicioIntervalo == null,
                            "Finalize o intervalo antes de registrar saída");

                    tempoTrabalhado = tempoTrabalhado.plus(
                            Duration.between(inicioTrabalho, horario)
                    );

                    inicioTrabalho = null;
                }
            }
        }

        exigir(inicioTrabalho == null,
                "A jornada ainda está em aberto");

        exigir(inicioIntervalo == null,
                "O intervalo ainda está em aberto");

        return new ResumoJornada(
                tempoTrabalhado,
                tempoIntervalo
        );
    }

    private void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}

package br.com.luismarangoni.jornada_api.marcacao.apuracao;

import java.time.Duration;

public record ResumoJornada(
        Duration tempoTrabalhado,
        Duration tempoIntervalo
) {
}

package br.com.luismarangoni.jornada_api.marcacao.apuracao;

import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApuradorJornadaTest {

    @Test
    void deveCalcularTempoTrabalhadoEIntervalo() {
        List<MarcacaoConsulta> marcacoes = List.of(
                marcacao(TipoMarcacao.ENTRADA, "08:00:00Z"),
                marcacao(TipoMarcacao.INICIO_INTERVALO, "12:00:00Z"),
                marcacao(TipoMarcacao.FIM_INTERVALO, "13:00:00Z"),
                marcacao(TipoMarcacao.SAIDA, "17:00:00Z")
        );

        ResumoJornada resultado =
                new ApuradorJornada().calcular(marcacoes);

        assertEquals(Duration.ofHours(8), resultado.tempoTrabalhado());
        assertEquals(Duration.ofHours(1), resultado.tempoIntervalo());
    }

    @Test
    void deveCalcularJornadaSemIntervalo() {
        List<MarcacaoConsulta> marcacoes = List.of(
                marcacao(TipoMarcacao.ENTRADA, "08:00:00Z"),
                marcacao(TipoMarcacao.SAIDA, "17:00:00Z")
        );

        ResumoJornada resultado =
                new ApuradorJornada().calcular(marcacoes);

        assertEquals(Duration.ofHours(9), resultado.tempoTrabalhado());
        assertEquals(Duration.ZERO, resultado.tempoIntervalo());
    }

    @Test
    void deveRejeitarJornadaEmAberto() {
        List<MarcacaoConsulta> marcacoes = List.of(
                marcacao(TipoMarcacao.ENTRADA, "08:00:00Z")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ApuradorJornada().calcular(marcacoes)
        );
    }

    private MarcacaoConsulta marcacao(
            TipoMarcacao tipo,
            String horario
    ) {
        return new MarcacaoConsulta(
                1L,
                1L,
                tipo,
                Instant.parse("2026-09-15T" + horario)
        );
    }
}

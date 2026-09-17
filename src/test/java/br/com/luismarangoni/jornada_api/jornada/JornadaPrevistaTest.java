package br.com.luismarangoni.jornada_api.jornada;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JornadaPrevistaTest {

    private final JornadaPrevista jornada = JornadaPrevista.padrao();

    @Test
    void deveConsiderar510MinutosDeSegundaASexta() {
        assertThat(jornada.minutosPrevistos(DayOfWeek.MONDAY)).isEqualTo(510);
        assertThat(jornada.minutosPrevistos(DayOfWeek.FRIDAY)).isEqualTo(510);
    }

    @Test
    void naoDeveConsiderarJornadaNoFimDeSemana() {
        assertThat(jornada.minutosPrevistos(DayOfWeek.SATURDAY)).isZero();
        assertThat(jornada.minutosPrevistos(DayOfWeek.SUNDAY)).isZero();
    }

    @Test
    void deveIgnorarDeficitDentroDaTolerancia() {
        assertThat(jornada.deficitDescontavel(502)).isZero();
    }

    @Test
    void deveRegistrarSomenteDeficitAcimaDaTolerancia() {
        assertThat(jornada.deficitDescontavel(490)).isEqualTo(10);
    }
}

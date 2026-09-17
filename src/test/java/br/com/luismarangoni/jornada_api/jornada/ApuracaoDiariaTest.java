package br.com.luismarangoni.jornada_api.jornada;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ApuracaoDiariaTest {

    @Test
    void deveConsiderarJornadaRegular() {
        var apuracao = ApuracaoDiaria.calcular(
                1L, LocalDate.of(2026, 9, 17), 510, 510);

        assertThat(apuracao.situacao()).isEqualTo(SituacaoApuracao.REGULAR);
        assertThat(apuracao.minutosDeficit()).isZero();
        assertThat(apuracao.ausente()).isFalse();
    }

    @Test
    void deveRegistrarDeficitAcimaDaTolerancia() {
        var apuracao = ApuracaoDiaria.calcular(
                1L, LocalDate.of(2026, 9, 17), 510, 490);

        assertThat(apuracao.situacao()).isEqualTo(SituacaoApuracao.DEFICIT);
        assertThat(apuracao.minutosDeficit()).isEqualTo(10);
    }

    @Test
    void deveIdentificarAusencia() {
        var apuracao = ApuracaoDiaria.calcular(
                1L, LocalDate.of(2026, 9, 17), 510, 0);

        assertThat(apuracao.situacao()).isEqualTo(SituacaoApuracao.AUSENTE);
        assertThat(apuracao.ausente()).isTrue();
    }
}

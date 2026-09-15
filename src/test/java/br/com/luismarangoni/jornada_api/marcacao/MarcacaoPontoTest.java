package br.com.luismarangoni.jornada_api.marcacao;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MarcacaoPontoTest {

    @Test
    void deveCriarMarcacaoValida() {
        Instant horario = Instant.parse("2026-09-15T12:00:00Z");

        MarcacaoPonto marcacao = new MarcacaoPonto(
                1L,
                TipoMarcacao.ENTRADA,
                horario
        );

        assertEquals(1L, marcacao.getFuncionarioId());
        assertEquals(TipoMarcacao.ENTRADA, marcacao.getTipo());
        assertEquals(horario, marcacao.getOcorridaEm());
    }

    @Test
    void deveRejeitarFuncionarioInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MarcacaoPonto(
                        0L,
                        TipoMarcacao.ENTRADA,
                        Instant.now()
                )
        );
    }

    @Test
    void deveRejeitarTipoAusente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MarcacaoPonto(
                        1L,
                        null,
                        Instant.now()
                )
        );
    }

    @Test
    void deveRejeitarHorarioAusente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MarcacaoPonto(
                        1L,
                        TipoMarcacao.ENTRADA,
                        null
                )
        );
    }
}

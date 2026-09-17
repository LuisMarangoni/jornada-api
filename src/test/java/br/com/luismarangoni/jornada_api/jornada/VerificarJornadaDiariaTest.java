package br.com.luismarangoni.jornada_api.jornada;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import br.com.luismarangoni.jornada_api.marcacao.apuracao.ApuradorJornada;
import br.com.luismarangoni.jornada_api.marcacao.apuracao.ApurarJornadaFuncionario;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class VerificarJornadaDiariaTest {

    private MarcacaoPontoRepository marcacaoRepository;
    private VerificarJornadaDiaria verificar;

    @BeforeEach
    void configurar() {
        var funcionarioRepository = Mockito.mock(FuncionarioRepository.class);
        marcacaoRepository = Mockito.mock(MarcacaoPontoRepository.class);

        when(funcionarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(
                        new FuncionarioConsulta(
                                1L, "MAT-001", "Luis",
                                "luis@email.com", true
                        )
                ));

        var apurador = new ApurarJornadaFuncionario(
                funcionarioRepository,
                marcacaoRepository,
                new ApuradorJornada()
        );

        verificar = new VerificarJornadaDiaria(
                apurador,
                JornadaPrevista.padrao()
        );
    }

    @Test
    void deveIdentificarJornadaRegular() {
        configurarMarcacoes(
                "08:00:00Z", "12:00:00Z",
                "14:00:00Z", "18:30:00Z"
        );

        var resultado = verificar.executar(
                1L, LocalDate.of(2026, 9, 17)
        );

        assertThat(resultado.situacao())
                .isEqualTo(SituacaoApuracao.REGULAR);
        assertThat(resultado.minutosDeficit()).isZero();
    }

    @Test
    void deveIdentificarAusencia() {
        when(marcacaoRepository.listarPorFuncionarioEPeriodo(
                eq(1L), any(Instant.class), any(Instant.class)
        )).thenReturn(List.of());

        var resultado = verificar.executar(
                1L, LocalDate.of(2026, 9, 17)
        );

        assertThat(resultado.situacao())
                .isEqualTo(SituacaoApuracao.AUSENTE);
        assertThat(resultado.ausente()).isTrue();
    }

    private void configurarMarcacoes(String entrada, String inicioIntervalo,
                                     String fimIntervalo, String saida) {
        var data = "2026-09-17T";

        when(marcacaoRepository.listarPorFuncionarioEPeriodo(
                eq(1L), any(Instant.class), any(Instant.class)
        )).thenReturn(List.of(
                new MarcacaoConsulta(
                        1L, 1L, TipoMarcacao.ENTRADA,
                        Instant.parse(data + entrada)
                ),
                new MarcacaoConsulta(
                        2L, 1L, TipoMarcacao.INICIO_INTERVALO,
                        Instant.parse(data + inicioIntervalo)
                ),
                new MarcacaoConsulta(
                        3L, 1L, TipoMarcacao.FIM_INTERVALO,
                        Instant.parse(data + fimIntervalo)
                ),
                new MarcacaoConsulta(
                        4L, 1L, TipoMarcacao.SAIDA,
                        Instant.parse(data + saida)
                )
        ));
    }

    @Test
    void deveIdentificarDeficitAcimaDaTolerancia() {
        configurarMarcacoes(
                "08:00:00Z", "12:00:00Z",
                "14:00:00Z", "18:10:00Z"
        );

        var resultado = verificar.executar(
                1L, LocalDate.of(2026, 9, 17)
        );

        assertThat(resultado.situacao())
                .isEqualTo(SituacaoApuracao.DEFICIT);
        assertThat(resultado.minutosDeficit()).isEqualTo(10);
    }

    @Test
    void deveConsiderarFimDeSemanaComoFolga() {
        when(marcacaoRepository.listarPorFuncionarioEPeriodo(
                eq(1L), any(Instant.class), any(Instant.class)
        )).thenReturn(List.of());

        var resultado = verificar.executar(
                1L, LocalDate.of(2026, 9, 19)
        );

        assertThat(resultado.situacao())
                .isEqualTo(SituacaoApuracao.REGULAR);
        assertThat(resultado.ausente()).isFalse();
    }
}

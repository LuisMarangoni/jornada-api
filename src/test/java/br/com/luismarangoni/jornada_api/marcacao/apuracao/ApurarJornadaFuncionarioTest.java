package br.com.luismarangoni.jornada_api.marcacao.apuracao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApurarJornadaFuncionarioTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private MarcacaoPontoRepository marcacaoRepository;

    @Test
    void deveApurarJornadaDoFuncionario() {
        when(funcionarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        when(marcacaoRepository.listarPorFuncionario(1L))
                .thenReturn(List.of(
                        marcacao(TipoMarcacao.ENTRADA, "08:00:00Z"),
                        marcacao(TipoMarcacao.SAIDA, "17:00:00Z")
                ));

        ApurarJornadaFuncionario apurar =
                new ApurarJornadaFuncionario(
                        funcionarioRepository,
                        marcacaoRepository,
                        new ApuradorJornada()
                );

        ResumoJornada resultado = apurar.executar(1L);

        assertEquals(Duration.ofHours(9), resultado.tempoTrabalhado());
        assertEquals(Duration.ZERO, resultado.tempoIntervalo());
    }

    @Test
    void deveRejeitarFuncionarioInexistente() {
        when(funcionarioRepository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        ApurarJornadaFuncionario apurar =
                new ApurarJornadaFuncionario(
                        funcionarioRepository,
                        marcacaoRepository,
                        new ApuradorJornada()
                );

        assertThrows(
                br.com.luismarangoni.jornada_api.funcionario.aplicacao
                        .FuncionarioNaoEncontradoException.class,
                () -> apurar.executar(999L)
        );

        verifyNoInteractions(marcacaoRepository);
    }

    private MarcacaoConsulta marcacao(
            TipoMarcacao tipo,
            String horario
    ) {
        String horarioCompleto = horario.contains("T")
                ? horario
                : "2026-09-15T" + horario;

        return new MarcacaoConsulta(
                1L,
                1L,
                tipo,
                Instant.parse(horarioCompleto)
        );
    }

    @Test
    void deveApurarJornadaDoPeriodoInformado() {
        Instant inicio = Instant.parse("2026-09-17T00:00:00Z");
        Instant fim = Instant.parse("2026-09-18T00:00:00Z");

        when(funcionarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        when(marcacaoRepository.listarPorFuncionarioEPeriodo(
                1L,
                inicio,
                fim
        )).thenReturn(List.of(
                marcacao(TipoMarcacao.ENTRADA, "2026-09-17T08:00:00Z"),
                marcacao(TipoMarcacao.SAIDA, "2026-09-17T17:00:00Z")
        ));

        ApurarJornadaFuncionario apurar =
                new ApurarJornadaFuncionario(
                        funcionarioRepository,
                        marcacaoRepository,
                        new ApuradorJornada()
                );

        ResumoJornada resultado =
                apurar.executar(1L, inicio, fim);

        assertEquals(Duration.ofHours(9), resultado.tempoTrabalhado());
        assertEquals(Duration.ZERO, resultado.tempoIntervalo());

        verify(marcacaoRepository).listarPorFuncionarioEPeriodo(
                1L,
                inicio,
                fim
        );
    }
}

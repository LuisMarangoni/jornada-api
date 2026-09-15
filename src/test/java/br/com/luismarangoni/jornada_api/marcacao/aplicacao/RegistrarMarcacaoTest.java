package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioInativoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.MarcacaoPonto;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarMarcacaoTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private MarcacaoPontoRepository marcacaoRepository;

    @Test
    void deveRegistrarMarcacaoParaFuncionarioAtivo() {
        Instant horario = Instant.parse("2026-09-15T12:00:00Z");

        when(funcionarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        when(marcacaoRepository.salvar(any()))
                .thenReturn(10L);

        RegistrarMarcacao registrar = new RegistrarMarcacao(
                funcionarioRepository,
                marcacaoRepository,
                Clock.fixed(horario, ZoneOffset.UTC)
        );

        MarcacaoConsulta resultado =
                registrar.executar(1L, TipoMarcacao.ENTRADA);

        assertEquals(10L, resultado.id());
        assertEquals(1L, resultado.funcionarioId());
        assertEquals(TipoMarcacao.ENTRADA, resultado.tipo());
        assertEquals(horario, resultado.ocorridaEm());

        ArgumentCaptor<MarcacaoPonto> captor =
                ArgumentCaptor.forClass(MarcacaoPonto.class);

        verify(marcacaoRepository).salvar(captor.capture());

        assertEquals(1L, captor.getValue().getFuncionarioId());
        assertEquals(TipoMarcacao.ENTRADA, captor.getValue().getTipo());
        assertEquals(horario, captor.getValue().getOcorridaEm());
    }

    @Test
    void deveRejeitarMarcacaoParaFuncionarioInativo() {
        when(funcionarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        false
                )));

        RegistrarMarcacao registrar = new RegistrarMarcacao(
                funcionarioRepository,
                marcacaoRepository,
                Clock.systemUTC()
        );

        assertThrows(
                FuncionarioInativoException.class,
                () -> registrar.executar(1L, TipoMarcacao.ENTRADA)
        );

        verifyNoInteractions(marcacaoRepository);
    }

    @Test
    void deveRejeitarMarcacaoQuandoFuncionarioNaoExistir() {
        when(funcionarioRepository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        RegistrarMarcacao registrar = new RegistrarMarcacao(
                funcionarioRepository,
                marcacaoRepository,
                Clock.systemUTC()
        );

        assertThrows(
                br.com.luismarangoni.jornada_api.funcionario.aplicacao
                        .FuncionarioNaoEncontradoException.class,
                () -> registrar.executar(999L, TipoMarcacao.ENTRADA)
        );

        verifyNoInteractions(marcacaoRepository);
    }

}

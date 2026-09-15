package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarMarcacoesFuncionarioTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private MarcacaoPontoRepository marcacaoRepository;

    @Test
    void deveListarMarcacoesDoFuncionario() {
        when(funcionarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        List<MarcacaoConsulta> esperadas = List.of(
                new MarcacaoConsulta(
                        10L,
                        1L,
                        TipoMarcacao.ENTRADA,
                        Instant.parse("2026-09-15T08:00:00Z")
                )
        );

        when(marcacaoRepository.listarPorFuncionario(1L))
                .thenReturn(esperadas);

        ListarMarcacoesFuncionario listar =
                new ListarMarcacoesFuncionario(
                        funcionarioRepository,
                        marcacaoRepository
                );

        List<MarcacaoConsulta> resultado = listar.executar(1L);

        assertEquals(esperadas, resultado);
    }

    @Test
    void deveRejeitarQuandoFuncionarioNaoExistir() {
        when(funcionarioRepository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        ListarMarcacoesFuncionario listar =
                new ListarMarcacoesFuncionario(
                        funcionarioRepository,
                        marcacaoRepository
                );

        assertThrows(
                br.com.luismarangoni.jornada_api.funcionario.aplicacao
                        .FuncionarioNaoEncontradoException.class,
                () -> listar.executar(999L)
        );

        verifyNoInteractions(marcacaoRepository);
    }
}

package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarFuncionarioTest {

    @Mock
    private FuncionarioRepository repository;

    @Test
    void deveRetornarFuncionarioQuandoIdExistir() {
        FuncionarioConsulta esperado = new FuncionarioConsulta(
                1L,
                "MAT-001",
                "Ana Silva",
                "ana@email.com",
                true
        );

        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(esperado));

        BuscarFuncionario buscarFuncionario =
                new BuscarFuncionario(repository);

        FuncionarioConsulta resultado =
                buscarFuncionario.executar(1L);

        assertEquals(esperado, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(repository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        BuscarFuncionario buscarFuncionario =
                new BuscarFuncionario(repository);

        FuncionarioNaoEncontradoException erro = assertThrows(
                FuncionarioNaoEncontradoException.class,
                () -> buscarFuncionario.executar(999L)
        );

        assertEquals(
                "Funcionário não encontrado: 999",
                erro.getMessage()
        );
    }
}

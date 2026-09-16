package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VincularUsuarioFuncionarioTest {

    @Mock
    private FuncionarioRepository repository;

    @Test
    void deveVincularUsuarioAoFuncionario() {
        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        when(repository.buscarPorUsuarioId(501L))
                .thenReturn(Optional.empty());

        VincularUsuarioFuncionario vincular =
                new VincularUsuarioFuncionario(repository);

        vincular.executar(1L, 501L);

        verify(repository).vincularUsuario(1L, 501L);
    }

    @Test
    void deveRejeitarUsuarioJaVinculado() {
        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        when(repository.buscarPorUsuarioId(501L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        2L,
                        "MAT-002",
                        "Bruno Souza",
                        "bruno@email.com",
                        true
                )));

        VincularUsuarioFuncionario vincular =
                new VincularUsuarioFuncionario(repository);

        assertThrows(
                UsuarioJaVinculadoException.class,
                () -> vincular.executar(1L, 501L)
        );

        verify(repository, never()).vincularUsuario(anyLong(), anyLong());
    }
}

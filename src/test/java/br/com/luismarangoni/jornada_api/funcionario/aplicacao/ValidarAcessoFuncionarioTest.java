package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarAcessoFuncionarioTest {

    @Mock
    private FuncionarioRepository repository;

    @Test
    void devePermitirAcessoAoFuncionarioVinculado() {
        when(repository.buscarPorUsuarioId(501L))
                .thenReturn(Optional.of(
                        new FuncionarioConsulta(
                                1L,
                                "MAT-001",
                                "Ana Silva",
                                "ana@email.com",
                                true
                        )
                ));

        ValidarAcessoFuncionario validar =
                new ValidarAcessoFuncionario(repository);

        assertDoesNotThrow(
                () -> validar.executar(1L, 501L)
        );
    }

    @Test
    void deveRejeitarAcessoAOutroFuncionario() {
        when(repository.buscarPorUsuarioId(501L))
                .thenReturn(Optional.of(
                        new FuncionarioConsulta(
                                2L,
                                "MAT-002",
                                "Bruno Souza",
                                "bruno@email.com",
                                true
                        )
                ));

        ValidarAcessoFuncionario validar =
                new ValidarAcessoFuncionario(repository);

        assertThrows(
                FuncionarioAcessoNegadoException.class,
                () -> validar.executar(1L, 501L)
        );
    }

    @Test
    void deveRejeitarUsuarioSemVinculo() {
        when(repository.buscarPorUsuarioId(999L))
                .thenReturn(Optional.empty());

        ValidarAcessoFuncionario validar =
                new ValidarAcessoFuncionario(repository);

        assertThrows(
                FuncionarioAcessoNegadoException.class,
                () -> validar.executar(1L, 999L)
        );
    }
}

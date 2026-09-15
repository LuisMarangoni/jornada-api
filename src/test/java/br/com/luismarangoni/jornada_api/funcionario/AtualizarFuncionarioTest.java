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
class AtualizarFuncionarioTest {

    @Mock
    private FuncionarioRepository repository;

    @Test
    void deveAtualizarNomeEEmail() {
        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        AtualizarFuncionario atualizar =
                new AtualizarFuncionario(repository);

        FuncionarioConsulta resultado = atualizar.executar(
                1L,
                "  Carlos Souza  ",
                "  CARLOS@EMAIL.COM  "
        );

        assertEquals("Carlos Souza", resultado.nome());
        assertEquals("carlos@email.com", resultado.email());
        assertEquals("MAT-001", resultado.matricula());
        assertTrue(resultado.ativo());

        verify(repository).atualizar(
                1L,
                "Carlos Souza",
                "carlos@email.com"
        );
    }

    @Test
    void deveRejeitarAtualizacaoQuandoFuncionarioNaoExistir() {
        when(repository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        AtualizarFuncionario atualizar =
                new AtualizarFuncionario(repository);

        assertThrows(
                FuncionarioNaoEncontradoException.class,
                () -> atualizar.executar(
                        999L,
                        "Carlos Souza",
                        "carlos@email.com"
                )
        );

        verify(repository, never()).atualizar(anyLong(), any(), any());
    }
}

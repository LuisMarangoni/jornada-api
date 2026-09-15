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
class AlterarStatusFuncionarioTest {

    @Mock
    private FuncionarioRepository repository;

    @Test
    void deveDesativarFuncionario() {
        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(new FuncionarioConsulta(
                        1L,
                        "MAT-001",
                        "Ana Silva",
                        "ana@email.com",
                        true
                )));

        AlterarStatusFuncionario alterar =
                new AlterarStatusFuncionario(repository);

        FuncionarioConsulta resultado = alterar.executar(1L, false);

        assertFalse(resultado.ativo());
        assertEquals("MAT-001", resultado.matricula());

        verify(repository).atualizarStatus(1L, false);
    }

    @Test
    void deveRejeitarAlteracaoQuandoFuncionarioNaoExistir() {
        when(repository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        AlterarStatusFuncionario alterar =
                new AlterarStatusFuncionario(repository);

        assertThrows(
                FuncionarioNaoEncontradoException.class,
                () -> alterar.executar(999L, false)
        );

        verify(repository, never()).atualizarStatus(anyLong(), anyBoolean());
    }
}

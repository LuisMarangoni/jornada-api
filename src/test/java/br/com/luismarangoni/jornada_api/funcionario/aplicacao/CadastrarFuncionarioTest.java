package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarFuncionarioTest {

    @Mock
    private FuncionarioRepository repository;

    @InjectMocks
    private CadastrarFuncionario cadastrarFuncionario;

    @Test
    void deveCadastrarFuncionarioComDadosNormalizados() {
        when(repository.existePorMatricula("MAT-001"))
                .thenReturn(false);

        when(repository.salvar(any(Funcionario.class)))
                .thenReturn(42L);

        Long id = cadastrarFuncionario.executar(
                "  MAT-001  ",
                "  Ana Silva  ",
                "  ANA@EMAIL.COM  "
        );

        assertEquals(Long.valueOf(42L), id);

        verify(repository).existePorMatricula("MAT-001");

        ArgumentCaptor<Funcionario> captor =
                ArgumentCaptor.forClass(Funcionario.class);

        verify(repository).salvar(captor.capture());

        Funcionario enviado = captor.getValue();

        assertEquals("MAT-001", enviado.getMatricula());
        assertEquals("Ana Silva", enviado.getNome());
        assertEquals("ana@email.com", enviado.getEmail());
        assertTrue(enviado.isAtivo());
    }

    @Test
    void deveImpedirCadastroComMatriculaDuplicada() {
        when(repository.existePorMatricula("MAT-001"))
                .thenReturn(true);

        MatriculaJaCadastradaException erro = assertThrows(
                MatriculaJaCadastradaException.class,
                () -> cadastrarFuncionario.executar(
                        "  MAT-001  ",
                        "Ana Silva",
                        "ana@email.com"
                )
        );

        assertEquals(
                "Matrícula já cadastrada: MAT-001",
                erro.getMessage()
        );

        verify(repository, never()).salvar(any(Funcionario.class));
    }

    @Test
    void deveRejeitarDadosInvalidosAntesDeAcessarRepositorio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cadastrarFuncionario.executar(
                        "MAT-001",
                        "   ",
                        "ana@email.com"
                )
        );

        verifyNoInteractions(repository);
    }
}

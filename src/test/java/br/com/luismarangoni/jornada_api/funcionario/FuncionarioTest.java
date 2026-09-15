package br.com.luismarangoni.jornada_api.funcionario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    @Test
    void deveCriarFuncionarioAtivoComDadosNormalizados() {
        Funcionario funcionario = new Funcionario(
                "  MAT-001  ",
                "  Ana Silva  ",
                "  ANA@EMAIL.COM  "
        );

        assertEquals("MAT-001", funcionario.getMatricula());
        assertEquals("Ana Silva", funcionario.getNome());
        assertEquals("ana@email.com", funcionario.getEmail());
        assertTrue(funcionario.isAtivo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRejeitarMatriculaAusente(String matricula) {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Funcionario(
                        matricula,
                        "Ana Silva",
                        "ana@email.com"
                )
        );

        assertEquals("Matrícula é obrigatório", erro.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRejeitarNomeAusente(String nome) {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Funcionario(
                        "MAT-001",
                        nome,
                        "ana@email.com"
                )
        );

        assertEquals("Nome é obrigatório", erro.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRejeitarEmailAusente(String email) {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Funcionario(
                        "MAT-001",
                        "Ana Silva",
                        email
                )
        );

        assertEquals("E-mail é obrigatório", erro.getMessage());
    }

    @Test
    void deveAtualizarNomeEEmailMantendoMatriculaEAtivo() {
        Funcionario funcionario = new Funcionario(
                "MAT-001",
                "Ana Silva",
                "ana@email.com"
        );

        funcionario.atualizarDados(
                "  Carlos Souza  ",
                "  CARLOS@EMAIL.COM  "
        );

        assertEquals("MAT-001", funcionario.getMatricula());
        assertEquals("Carlos Souza", funcionario.getNome());
        assertEquals("carlos@email.com", funcionario.getEmail());
        assertTrue(funcionario.isAtivo());
    }

}

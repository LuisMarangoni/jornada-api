package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.MatriculaJaCadastradaException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.PaginaFuncionarios;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
@Transactional
class FuncionarioRepositoryAdapterTest {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private FuncionarioJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void devePersistirFuncionarioDoDominio() {
        assertFalse(repository.existePorMatricula("MAT-101"));

        Funcionario funcionario = new Funcionario(
                "  MAT-101  ",
                "  Ana Silva  ",
                "  ANA@EMAIL.COM  "
        );

        Long id = repository.salvar(funcionario);

        assertNotNull(id);
        entityManager.clear();

        FuncionarioJpaEntity encontrado = jpaRepository.findById(id)
                .orElseThrow();

        assertEquals("MAT-101", encontrado.getMatricula());
        assertEquals("Ana Silva", encontrado.getNome());
        assertEquals("ana@email.com", encontrado.getEmail());
        assertTrue(encontrado.isAtivo());
        assertTrue(repository.existePorMatricula("MAT-101"));
    }

    @Test
    void deveTraduzirViolacaoDeMatriculaDuplicada() {
        repository.salvar(new Funcionario(
                "MAT-102",
                "Ana Silva",
                "ana@email.com"
        ));

        Funcionario duplicado = new Funcionario(
                "MAT-102",
                "Bruno Souza",
                "bruno@email.com"
        );

        MatriculaJaCadastradaException erro = assertThrows(
                MatriculaJaCadastradaException.class,
                () -> repository.salvar(duplicado)
        );

        assertEquals(
                "Matrícula já cadastrada: MAT-102",
                erro.getMessage()
        );

        assertInstanceOf(
                DataIntegrityViolationException.class,
                erro.getCause()
        );
    }

    @Test
    void naoDeveConfundirOutroErroComMatriculaDuplicada() {
        Funcionario funcionario = new Funcionario(
                "MAT-103",
                "A".repeat(151),
                "ana@email.com"
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.salvar(funcionario)
        );
    }

    @Test
    void deveConsultarFuncionarioPreservandoEstadoInativo() {
        FuncionarioJpaEntity salvo = jpaRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-104",
                        "Ana Silva",
                        "ana@email.com",
                        false
                )
        );

        Long id = salvo.getId();
        entityManager.clear();

        FuncionarioConsulta encontrado = repository.buscarPorId(id)
                .orElseThrow();

        assertEquals(id, encontrado.id());
        assertEquals("MAT-104", encontrado.matricula());
        assertEquals("Ana Silva", encontrado.nome());
        assertEquals("ana@email.com", encontrado.email());
        assertFalse(encontrado.ativo());
    }

    @Test
    void deveRetornarVazioQuandoFuncionarioNaoExiste() {
        assertTrue(repository.buscarPorId(Long.MAX_VALUE).isEmpty());
    }

    @Test
    void deveListarFuncionariosPaginadosPorId() {
        repository.salvar(new Funcionario(
                "MAT-201",
                "Ana Silva",
                "ana@email.com"
        ));

        repository.salvar(new Funcionario(
                "MAT-202",
                "Bruno Souza",
                "bruno@email.com"
        ));

        repository.salvar(new Funcionario(
                "MAT-203",
                "Carla Lima",
                "carla@email.com"
        ));

        PaginaFuncionarios primeiraPagina =
                repository.listar(0, 2);

        assertEquals(2, primeiraPagina.conteudo().size());
        assertEquals(0, primeiraPagina.pagina());
        assertEquals(2, primeiraPagina.tamanho());
        assertEquals(3, primeiraPagina.totalElementos());
        assertEquals(2, primeiraPagina.totalPaginas());

        assertEquals(
                "MAT-201",
                primeiraPagina.conteudo().get(0).matricula()
        );
        assertEquals(
                "MAT-202",
                primeiraPagina.conteudo().get(1).matricula()
        );

        PaginaFuncionarios segundaPagina =
                repository.listar(1, 2);

        assertEquals(1, segundaPagina.conteudo().size());
        assertEquals("MAT-203",
                segundaPagina.conteudo().get(0).matricula());
    }

}

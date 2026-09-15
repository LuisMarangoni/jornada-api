package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
@Transactional
class FuncionarioJpaRepositoryTest {

    @Autowired
    private FuncionarioJpaRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarEConsultarFuncionario() {
        FuncionarioJpaEntity funcionario = new FuncionarioJpaEntity(
                "MAT-001",
                "Ana Silva",
                "ana@email.com",
                true
        );

        FuncionarioJpaEntity salvo = repository.saveAndFlush(funcionario);

        assertNotNull(salvo.getId());

        Long id = salvo.getId();
        entityManager.clear();

        FuncionarioJpaEntity encontrado = repository.findById(id)
                .orElseThrow();

        assertEquals(id, encontrado.getId());
        assertEquals("MAT-001", encontrado.getMatricula());
        assertEquals("Ana Silva", encontrado.getNome());
        assertEquals("ana@email.com", encontrado.getEmail());
        assertTrue(encontrado.isAtivo());
    }

    @Test
    void deveRejeitarMatriculaDuplicada() {
        repository.saveAndFlush(new FuncionarioJpaEntity(
                "MAT-002",
                "Ana Silva",
                "ana@email.com",
                true
        ));

        FuncionarioJpaEntity duplicado = new FuncionarioJpaEntity(
                "MAT-002",
                "Bruno Souza",
                "bruno@email.com",
                true
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(duplicado)
        );
    }
}
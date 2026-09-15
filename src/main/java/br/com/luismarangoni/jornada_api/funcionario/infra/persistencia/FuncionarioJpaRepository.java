package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioJpaRepository
        extends JpaRepository<FuncionarioJpaEntity, Long> {

    boolean existsByMatricula(String matricula);

    Optional<FuncionarioJpaEntity> findByMatricula(String matricula);

}

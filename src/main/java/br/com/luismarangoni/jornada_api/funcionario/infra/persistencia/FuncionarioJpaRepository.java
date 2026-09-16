package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioJpaRepository
        extends JpaRepository<FuncionarioJpaEntity, Long> {

    boolean existsByMatricula(String matricula);

    Page<FuncionarioJpaEntity> findAllByOrderByIdAsc(Pageable pageable);
    Optional<FuncionarioJpaEntity> findByMatricula(String matricula);
    Optional<FuncionarioJpaEntity> findByUsuarioId(Long usuarioId);

}

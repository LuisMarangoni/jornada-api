package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioJpaRepository
        extends JpaRepository<FuncionarioJpaEntity, Long> {
}

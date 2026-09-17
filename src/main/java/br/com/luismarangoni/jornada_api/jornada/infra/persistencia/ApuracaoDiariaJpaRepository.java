package br.com.luismarangoni.jornada_api.jornada.infra.persistencia;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApuracaoDiariaJpaRepository
        extends JpaRepository<ApuracaoDiariaJpaEntity, Long> {

    Optional<ApuracaoDiariaJpaEntity> findByFuncionarioIdAndData(
            Long funcionarioId,
            LocalDate data
    );
}

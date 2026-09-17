package br.com.luismarangoni.jornada_api.marcacao.infra.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MarcacaoPontoJpaRepository
        extends JpaRepository<MarcacaoPontoJpaEntity, Long> {

    List<MarcacaoPontoJpaEntity>
    findAllByFuncionarioIdOrderByOcorridaEmAsc(Long funcionarioId);
    @Query("""
        SELECT m
        FROM MarcacaoPontoJpaEntity m
        WHERE m.funcionarioId = :funcionarioId
          AND m.ocorridaEm >= :inicio
          AND m.ocorridaEm < :fim
        ORDER BY m.ocorridaEm ASC
        """)
    List<MarcacaoPontoJpaEntity> buscarPorFuncionarioEPeriodo(
            @Param("funcionarioId") Long funcionarioId,
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

}

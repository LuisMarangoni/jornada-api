package br.com.luismarangoni.jornada_api.marcacao.infra.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcacaoPontoJpaRepository
        extends JpaRepository<MarcacaoPontoJpaEntity, Long> {

    List<MarcacaoPontoJpaEntity>
    findAllByFuncionarioIdOrderByOcorridaEmAsc(Long funcionarioId);
}

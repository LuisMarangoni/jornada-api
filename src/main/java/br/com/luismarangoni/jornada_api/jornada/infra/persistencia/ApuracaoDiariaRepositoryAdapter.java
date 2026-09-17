package br.com.luismarangoni.jornada_api.jornada.infra.persistencia;

import br.com.luismarangoni.jornada_api.jornada.ApuracaoDiaria;
import br.com.luismarangoni.jornada_api.jornada.aplicacao.porta.ApuracaoDiariaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ApuracaoDiariaRepositoryAdapter
        implements ApuracaoDiariaRepository {

    private final ApuracaoDiariaJpaRepository repository;

    public ApuracaoDiariaRepositoryAdapter(
            ApuracaoDiariaJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public ApuracaoDiaria salvar(ApuracaoDiaria apuracao) {
        return repository.save(
                new ApuracaoDiariaJpaEntity(apuracao)
        ).toDomain();
    }

    @Override
    public Optional<ApuracaoDiaria> buscarPorFuncionarioEData(
            Long funcionarioId,
            LocalDate data
    ) {
        return repository.findByFuncionarioIdAndData(funcionarioId, data)
                .map(ApuracaoDiariaJpaEntity::toDomain);
    }
}

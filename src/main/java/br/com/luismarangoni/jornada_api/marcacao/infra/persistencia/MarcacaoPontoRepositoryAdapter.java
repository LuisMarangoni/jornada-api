package br.com.luismarangoni.jornada_api.marcacao.infra.persistencia;

import br.com.luismarangoni.jornada_api.marcacao.MarcacaoPonto;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public class MarcacaoPontoRepositoryAdapter
        implements MarcacaoPontoRepository {

    private final MarcacaoPontoJpaRepository jpaRepository;

    public MarcacaoPontoRepositoryAdapter(
            MarcacaoPontoJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<MarcacaoConsulta> listarPorFuncionarioEPeriodo(
            Long funcionarioId,
            Instant inicio,
            Instant fim
    ) {
        return jpaRepository
                .buscarPorFuncionarioEPeriodo(
                        funcionarioId,
                        inicio,
                        fim
                )
                .stream()
                .map(entidade -> new MarcacaoConsulta(
                        entidade.getId(),
                        entidade.getFuncionarioId(),
                        entidade.getTipo(),
                        entidade.getOcorridaEm()
                ))
                .toList();
    }

    @Override
    public Long salvar(MarcacaoPonto marcacao) {
        MarcacaoPontoJpaEntity entidade =
                new MarcacaoPontoJpaEntity(
                        marcacao.getFuncionarioId(),
                        marcacao.getTipo(),
                        marcacao.getOcorridaEm()
                );

        return jpaRepository.saveAndFlush(entidade).getId();
    }

    @Override
    public List<MarcacaoConsulta> listarPorFuncionario(
            Long funcionarioId
    ) {
        return jpaRepository
                .findAllByFuncionarioIdOrderByOcorridaEmAsc(funcionarioId)
                .stream()
                .map(entidade -> new MarcacaoConsulta(
                        entidade.getId(),
                        entidade.getFuncionarioId(),
                        entidade.getTipo(),
                        entidade.getOcorridaEm()
                ))
                .toList();
    }


}

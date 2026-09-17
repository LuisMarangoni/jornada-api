package br.com.luismarangoni.jornada_api.jornada.aplicacao.porta;

import br.com.luismarangoni.jornada_api.jornada.ApuracaoDiaria;

import java.time.LocalDate;
import java.util.Optional;

public interface ApuracaoDiariaRepository {

    ApuracaoDiaria salvar(ApuracaoDiaria apuracao);

    Optional<ApuracaoDiaria> buscarPorFuncionarioEData(
            Long funcionarioId,
            LocalDate data
    );
}

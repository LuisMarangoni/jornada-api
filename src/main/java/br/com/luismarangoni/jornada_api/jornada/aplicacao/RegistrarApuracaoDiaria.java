package br.com.luismarangoni.jornada_api.jornada.aplicacao;

import br.com.luismarangoni.jornada_api.jornada.ApuracaoDiaria;
import br.com.luismarangoni.jornada_api.jornada.VerificarJornadaDiaria;
import br.com.luismarangoni.jornada_api.jornada.aplicacao.porta.ApuracaoDiariaRepository;
import java.time.LocalDate;

public class RegistrarApuracaoDiaria {

    private final VerificarJornadaDiaria verificar;
    private final ApuracaoDiariaRepository repository;

    public RegistrarApuracaoDiaria(
            VerificarJornadaDiaria verificar,
            ApuracaoDiariaRepository repository
    ) {
        this.verificar = verificar;
        this.repository = repository;
    }

    public ApuracaoDiaria executar(
            Long funcionarioId,
            LocalDate data
    ) {
        var existente = repository.buscarPorFuncionarioEData(
                funcionarioId,
                data
        );

        if (existente.isPresent()) {
            return existente.get();
        }

        var apuracao = verificar.executar(funcionarioId, data);
        return repository.salvar(apuracao);
    }
}

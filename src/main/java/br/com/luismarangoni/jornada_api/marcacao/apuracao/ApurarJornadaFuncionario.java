package br.com.luismarangoni.jornada_api.marcacao.apuracao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioNaoEncontradoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;

import java.util.List;

public class ApurarJornadaFuncionario {

    private final FuncionarioRepository funcionarioRepository;
    private final MarcacaoPontoRepository marcacaoRepository;
    private final ApuradorJornada apurador;

    public ApurarJornadaFuncionario(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository,
            ApuradorJornada apurador
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.marcacaoRepository = marcacaoRepository;
        this.apurador = apurador;
    }

    public ResumoJornada executar(Long funcionarioId) {
        funcionarioRepository.buscarPorId(funcionarioId)
                .orElseThrow(
                        () -> new FuncionarioNaoEncontradoException(
                                funcionarioId
                        )
                );

        List<MarcacaoConsulta> marcacoes =
                marcacaoRepository.listarPorFuncionario(funcionarioId);

        return apurador.calcular(marcacoes);
    }
}

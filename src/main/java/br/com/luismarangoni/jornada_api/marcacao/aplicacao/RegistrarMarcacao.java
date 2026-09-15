package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioInativoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioNaoEncontradoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.MarcacaoPonto;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;

import java.time.Clock;

public class RegistrarMarcacao {

    private final FuncionarioRepository funcionarioRepository;
    private final MarcacaoPontoRepository marcacaoRepository;
    private final Clock clock;

    public RegistrarMarcacao(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository,
            Clock clock
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.marcacaoRepository = marcacaoRepository;
        this.clock = clock;
    }

    public MarcacaoConsulta executar(Long funcionarioId, TipoMarcacao tipo) {
        FuncionarioConsulta funcionario =
                funcionarioRepository.buscarPorId(funcionarioId)
                        .orElseThrow(
                                () -> new FuncionarioNaoEncontradoException(
                                        funcionarioId
                                )
                        );

        if (!funcionario.ativo()) {
            throw new FuncionarioInativoException(funcionarioId);
        }

        MarcacaoPonto marcacao = new MarcacaoPonto(
                funcionarioId,
                tipo,
                clock.instant()
        );

        Long id = marcacaoRepository.salvar(marcacao);

        return new MarcacaoConsulta(
                id,
                funcionarioId,
                tipo,
                marcacao.getOcorridaEm()
        );
    }
}

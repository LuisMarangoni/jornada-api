package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioNaoEncontradoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;

import java.util.List;

public class ListarMarcacoesFuncionario {

    private final FuncionarioRepository funcionarioRepository;
    private final MarcacaoPontoRepository marcacaoRepository;

    public ListarMarcacoesFuncionario(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.marcacaoRepository = marcacaoRepository;
    }

    public List<MarcacaoConsulta> executar(Long funcionarioId) {
        funcionarioRepository.buscarPorId(funcionarioId)
                .orElseThrow(
                        () -> new FuncionarioNaoEncontradoException(
                                funcionarioId
                        )
                );

        return marcacaoRepository.listarPorFuncionario(funcionarioId);
    }
}

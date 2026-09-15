package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class BuscarFuncionario {

    private final FuncionarioRepository repository;

    public BuscarFuncionario(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public FuncionarioConsulta executar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(
                        () -> new FuncionarioNaoEncontradoException(id)
                );
    }
}

package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class AlterarStatusFuncionario {

    private final FuncionarioRepository repository;

    public AlterarStatusFuncionario(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public FuncionarioConsulta executar(Long id, boolean ativo) {
        FuncionarioConsulta atual = repository.buscarPorId(id)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(id));

        repository.atualizarStatus(id, ativo);

        return new FuncionarioConsulta(
                atual.id(),
                atual.matricula(),
                atual.nome(),
                atual.email(),
                ativo
        );
    }
}

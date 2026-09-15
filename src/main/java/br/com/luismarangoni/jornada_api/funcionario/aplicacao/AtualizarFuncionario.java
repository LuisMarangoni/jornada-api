package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class AtualizarFuncionario {

    private final FuncionarioRepository repository;

    public AtualizarFuncionario(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public FuncionarioConsulta executar(
            Long id,
            String nome,
            String email
    ) {
        FuncionarioConsulta atual = repository.buscarPorId(id)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(id));

        Funcionario funcionario = new Funcionario(
                atual.matricula(),
                nome,
                email
        );

        repository.atualizar(
                id,
                funcionario.getNome(),
                funcionario.getEmail()
        );

        return new FuncionarioConsulta(
                id,
                atual.matricula(),
                funcionario.getNome(),
                funcionario.getEmail(),
                atual.ativo()
        );
    }
}

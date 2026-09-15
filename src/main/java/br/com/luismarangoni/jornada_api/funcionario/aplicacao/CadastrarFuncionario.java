package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class CadastrarFuncionario {

    private final FuncionarioRepository repository;

    public CadastrarFuncionario(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public Long executar(
            String matricula,
            String nome,
            String email
    ) {
        Funcionario funcionario = new Funcionario(
                matricula,
                nome,
                email
        );

        if (repository.existePorMatricula(funcionario.getMatricula())) {
            throw new MatriculaJaCadastradaException(
                    funcionario.getMatricula()
            );
        }

        return repository.salvar(funcionario);
    }
}

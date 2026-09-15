package br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;

public interface FuncionarioRepository {

    boolean existePorMatricula(String matricula);

    Long salvar(Funcionario funcionario);
}

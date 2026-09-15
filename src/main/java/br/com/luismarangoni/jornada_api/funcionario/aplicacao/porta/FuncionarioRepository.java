package br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;

import java.util.Optional;

public interface FuncionarioRepository {

    boolean existePorMatricula(String matricula);

    Long salvar(Funcionario funcionario);

    Optional<FuncionarioConsulta> buscarPorId(Long id);
}

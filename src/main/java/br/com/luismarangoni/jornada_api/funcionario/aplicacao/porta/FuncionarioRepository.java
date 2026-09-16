package br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.PaginaFuncionarios;

import java.util.Optional;

public interface FuncionarioRepository {

    void atualizar(Long id, String nome, String email);

    void atualizarStatus(Long id, boolean ativo);

    void vincularUsuario(Long funcionarioId, Long usuarioId);

    boolean existePorMatricula(String matricula);

    Long salvar(Funcionario funcionario);
    PaginaFuncionarios listar(int pagina, int tamanho);
    Optional<FuncionarioConsulta> buscarPorId(Long id);
    Optional<FuncionarioConsulta> buscarPorUsuarioId(Long usuarioId);
}

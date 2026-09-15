package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

public class FuncionarioNaoEncontradoException
        extends RuntimeException {

    public FuncionarioNaoEncontradoException(Long id) {
        super("Funcionário não encontrado: " + id);
    }
}

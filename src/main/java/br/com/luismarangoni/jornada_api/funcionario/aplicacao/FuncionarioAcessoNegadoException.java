package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

public class FuncionarioAcessoNegadoException
        extends RuntimeException {

    public FuncionarioAcessoNegadoException(Long funcionarioId) {
        super(
                "Usuário não possui acesso ao funcionário: "
                        + funcionarioId
        );
    }
}

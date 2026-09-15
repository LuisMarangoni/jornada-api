package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

public class FuncionarioInativoException extends RuntimeException {

    public FuncionarioInativoException(Long id) {
        super("Funcionário está inativo: " + id);
    }
}

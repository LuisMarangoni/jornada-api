package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

public class MatriculaJaCadastradaException extends RuntimeException {

    public MatriculaJaCadastradaException(String matricula) {
        super("Matrícula já cadastrada: " + matricula);
    }

    public MatriculaJaCadastradaException(
            String matricula,
            Throwable causa
    ) {
        super("Matrícula já cadastrada: " + matricula, causa);
    }

}

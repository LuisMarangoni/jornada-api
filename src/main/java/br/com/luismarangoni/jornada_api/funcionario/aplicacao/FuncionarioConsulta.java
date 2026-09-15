package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

public record FuncionarioConsulta(
        Long id,
        String matricula,
        String nome,
        String email,
        boolean ativo
) {
}
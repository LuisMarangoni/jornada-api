package br.com.luismarangoni.jornada_api.funcionario.api.dto;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;

public record FuncionarioResponse(
        Long id,
        String matricula,
        String nome,
        String email,
        boolean ativo
) {
    public static FuncionarioResponse from(
            FuncionarioConsulta funcionario
    ) {
        return new FuncionarioResponse(
                funcionario.id(),
                funcionario.matricula(),
                funcionario.nome(),
                funcionario.email(),
                funcionario.ativo()
        );
    }
}

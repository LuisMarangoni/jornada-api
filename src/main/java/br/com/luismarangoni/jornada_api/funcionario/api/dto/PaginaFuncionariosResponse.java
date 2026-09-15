package br.com.luismarangoni.jornada_api.funcionario.api.dto;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.PaginaFuncionarios;

import java.util.List;

public record PaginaFuncionariosResponse(
        List<FuncionarioResponse> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {
    public static PaginaFuncionariosResponse from(
            PaginaFuncionarios resultado
    ) {
        List<FuncionarioResponse> funcionarios =
                resultado.conteudo()
                        .stream()
                        .map(FuncionarioResponse::from)
                        .toList();

        return new PaginaFuncionariosResponse(
                funcionarios,
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalElementos(),
                resultado.totalPaginas()
        );
    }
}

package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import java.util.List;

public record PaginaFuncionarios(
        List<FuncionarioConsulta> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {
}

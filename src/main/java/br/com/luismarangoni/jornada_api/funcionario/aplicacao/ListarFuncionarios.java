package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class ListarFuncionarios {

    private final FuncionarioRepository repository;

    public ListarFuncionarios(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public PaginaFuncionarios executar(int pagina, int tamanho) {
        return repository.listar(pagina, tamanho);
    }
}

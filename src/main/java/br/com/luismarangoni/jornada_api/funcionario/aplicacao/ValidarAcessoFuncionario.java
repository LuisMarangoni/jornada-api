package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class ValidarAcessoFuncionario {

    private final FuncionarioRepository repository;

    public ValidarAcessoFuncionario(
            FuncionarioRepository repository
    ) {
        this.repository = repository;
    }

    public void executar(
            Long funcionarioId,
            Long usuarioId
    ) {
        boolean possuiAcesso = repository
                .buscarPorUsuarioId(usuarioId)
                .map(funcionario ->
                        funcionario.id().equals(funcionarioId)
                )
                .orElse(false);

        if (!possuiAcesso) {
            throw new FuncionarioAcessoNegadoException(
                    funcionarioId
            );
        }
    }
}

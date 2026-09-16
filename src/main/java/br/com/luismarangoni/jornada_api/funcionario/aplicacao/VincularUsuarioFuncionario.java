package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;

public class VincularUsuarioFuncionario {

    private final FuncionarioRepository repository;

    public VincularUsuarioFuncionario(
            FuncionarioRepository repository
    ) {
        this.repository = repository;
    }

    public void executar(
            Long funcionarioId,
            Long usuarioId
    ) {
        repository.buscarPorId(funcionarioId)
                .orElseThrow(
                        () -> new FuncionarioNaoEncontradoException(
                                funcionarioId
                        )
                );

        if (repository.buscarPorUsuarioId(usuarioId).isPresent()) {
            throw new UsuarioJaVinculadoException(usuarioId);
        }

        repository.vincularUsuario(
                funcionarioId,
                usuarioId
        );
    }
}

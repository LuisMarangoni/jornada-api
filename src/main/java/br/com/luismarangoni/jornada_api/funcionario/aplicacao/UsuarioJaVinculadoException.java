package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

public class UsuarioJaVinculadoException
        extends RuntimeException {

    public UsuarioJaVinculadoException(Long usuarioId) {
        super(
                "O usuário já está vinculado a um funcionário: "
                        + usuarioId
        );
    }
}

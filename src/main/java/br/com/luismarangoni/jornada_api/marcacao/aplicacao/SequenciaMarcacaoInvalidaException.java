package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

public class SequenciaMarcacaoInvalidaException
        extends RuntimeException {

    public SequenciaMarcacaoInvalidaException(
            Long funcionarioId,
            String mensagem
    ) {
        super(
                "Sequência de marcação inválida para o funcionário "
                        + funcionarioId + ": " + mensagem
        );
    }
}

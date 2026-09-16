package br.com.luismarangoni.jornada_api.marcacao.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioInativoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioNaoEncontradoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.marcacao.MarcacaoPonto;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;

import java.time.Clock;

public class RegistrarMarcacao {

    private final FuncionarioRepository funcionarioRepository;
    private final MarcacaoPontoRepository marcacaoRepository;
    private final Clock clock;

    public RegistrarMarcacao(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository,
            Clock clock
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.marcacaoRepository = marcacaoRepository;
        this.clock = clock;
    }

    public MarcacaoConsulta executar(
            Long funcionarioId,
            TipoMarcacao tipo
    ) {
        FuncionarioConsulta funcionario =
                funcionarioRepository.buscarPorId(funcionarioId)
                        .orElseThrow(
                                () -> new FuncionarioNaoEncontradoException(
                                        funcionarioId
                                )
                        );

        if (!funcionario.ativo()) {
            throw new FuncionarioInativoException(funcionarioId);
        }

        validarSequencia(funcionarioId, tipo);

        MarcacaoPonto marcacao = new MarcacaoPonto(
                funcionarioId,
                tipo,
                clock.instant()
        );

        Long id = marcacaoRepository.salvar(marcacao);

        return new MarcacaoConsulta(
                id,
                funcionarioId,
                tipo,
                marcacao.getOcorridaEm()
        );
    }

    private void validarSequencia(
            Long funcionarioId,
            TipoMarcacao novaMarcacao
    ) {
        var anteriores = marcacaoRepository
                .listarPorFuncionario(funcionarioId);

        if (anteriores.isEmpty()) {
            if (novaMarcacao != TipoMarcacao.ENTRADA) {
                throw new SequenciaMarcacaoInvalidaException(
                        funcionarioId,
                        "a primeira marcação deve ser ENTRADA"
                );
            }

            return;
        }

        TipoMarcacao ultimaMarcacao = anteriores
                .get(anteriores.size() - 1)
                .tipo();

        boolean permitida = switch (ultimaMarcacao) {
            case ENTRADA ->
                    novaMarcacao == TipoMarcacao.INICIO_INTERVALO
                            || novaMarcacao == TipoMarcacao.SAIDA;

            case INICIO_INTERVALO ->
                    novaMarcacao == TipoMarcacao.FIM_INTERVALO;

            case FIM_INTERVALO ->
                    novaMarcacao == TipoMarcacao.INICIO_INTERVALO
                            || novaMarcacao == TipoMarcacao.SAIDA;

            case SAIDA ->
                    novaMarcacao == TipoMarcacao.ENTRADA;
        };

        if (!permitida) {
            throw new SequenciaMarcacaoInvalidaException(
                    funcionarioId,
                    "não é permitido registrar "
                            + novaMarcacao
                            + " após "
                            + ultimaMarcacao
            );
        }
    }
}
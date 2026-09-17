package br.com.luismarangoni.jornada_api.marcacao.infra.persistencia;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import br.com.luismarangoni.jornada_api.funcionario.infra.persistencia.FuncionarioJpaEntity;
import br.com.luismarangoni.jornada_api.funcionario.infra.persistencia.FuncionarioJpaRepository;
import br.com.luismarangoni.jornada_api.marcacao.MarcacaoPonto;
import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
@Transactional
class MarcacaoPontoRepositoryAdapterTest {

    @Autowired
    private MarcacaoPontoRepository repository;

    @Autowired
    private FuncionarioJpaRepository funcionarioRepository;

    @Autowired
    private MarcacaoPontoJpaRepository jpaRepository;

    @Test
    void deveSalvarEListarMarcacoesPorHorario() {
        FuncionarioJpaEntity funcionario =
                funcionarioRepository.saveAndFlush(
                        new FuncionarioJpaEntity(
                                "MAT-701",
                                "Ana Silva",
                                "ana@email.com",
                                true
                        )
                );

        Instant entrada = Instant.parse("2026-09-15T08:00:00Z");
        Instant saida = Instant.parse("2026-09-15T17:00:00Z");

        Long entradaId = repository.salvar(new MarcacaoPonto(
                funcionario.getId(),
                TipoMarcacao.ENTRADA,
                entrada
        ));

        Long saidaId = repository.salvar(new MarcacaoPonto(
                funcionario.getId(),
                TipoMarcacao.SAIDA,
                saida
        ));

        assertNotNull(entradaId);
        assertNotNull(saidaId);

        jpaRepository.flush();

        List<MarcacaoConsulta> marcacoes =
                repository.listarPorFuncionario(funcionario.getId());

        assertEquals(2, marcacoes.size());
        assertEquals(TipoMarcacao.ENTRADA, marcacoes.get(0).tipo());
        assertEquals(entrada, marcacoes.get(0).ocorridaEm());
        assertEquals(TipoMarcacao.SAIDA, marcacoes.get(1).tipo());
        assertEquals(saida, marcacoes.get(1).ocorridaEm());
    }

    @Test
    void deveListarMarcacoesApenasDoPeriodoInformado() {
        var funcionario = funcionarioRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-1701",
                        "Ana Silva",
                        "ana1701@email.com",
                        true
                )
        );

        Long funcionarioId = funcionario.getId();

        repository.salvar(new MarcacaoPonto(
                funcionarioId,
                TipoMarcacao.ENTRADA,
                Instant.parse("2026-09-17T08:00:00Z")
        ));

        repository.salvar(new MarcacaoPonto(
                funcionarioId,
                TipoMarcacao.SAIDA,
                Instant.parse("2026-09-17T17:00:00Z")
        ));

        repository.salvar(new MarcacaoPonto(
                funcionarioId,
                TipoMarcacao.ENTRADA,
                Instant.parse("2026-09-18T08:00:00Z")
        ));

        Instant inicio = Instant.parse("2026-09-17T00:00:00Z");
        Instant fim = Instant.parse("2026-09-18T00:00:00Z");

        List<MarcacaoConsulta> resultado =
                repository.listarPorFuncionarioEPeriodo(
                        funcionarioId,
                        inicio,
                        fim
                );

        assertEquals(2, resultado.size());
        assertEquals(
                Instant.parse("2026-09-17T08:00:00Z"),
                resultado.get(0).ocorridaEm()
        );
        assertEquals(
                Instant.parse("2026-09-17T17:00:00Z"),
                resultado.get(1).ocorridaEm()
        );
    }
}

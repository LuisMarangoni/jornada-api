package br.com.luismarangoni.jornada_api.jornada.infra.persistencia;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import br.com.luismarangoni.jornada_api.jornada.ApuracaoDiaria;
import br.com.luismarangoni.jornada_api.jornada.SituacaoApuracao;
import br.com.luismarangoni.jornada_api.jornada.aplicacao.porta.ApuracaoDiariaRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
@Transactional
class ApuracaoDiariaRepositoryAdapterTest {

    @Autowired
    private ApuracaoDiariaRepository repository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Test
    void devePersistirEConsultarApuracaoDiaria() {
        Long funcionarioId = funcionarioRepository.salvar(
                new Funcionario(
                        "MAT-AP-001",
                        "Ana Silva",
                        "ana.apuracao@email.com"
                )
        );

        var data = LocalDate.of(2026, 9, 17);

        var apuracao = new ApuracaoDiaria(
                funcionarioId,
                data,
                510,
                490,
                10,
                false,
                SituacaoApuracao.DEFICIT
        );

        var salva = repository.salvar(apuracao);

        var encontrada = repository.buscarPorFuncionarioEData(
                funcionarioId,
                data
        );

        assertThat(salva).isEqualTo(apuracao);
        assertThat(encontrada).isPresent();
        assertThat(encontrada.orElseThrow().situacao())
                .isEqualTo(SituacaoApuracao.DEFICIT);
        assertThat(encontrada.orElseThrow().minutosDeficit())
                .isEqualTo(10);
    }
}

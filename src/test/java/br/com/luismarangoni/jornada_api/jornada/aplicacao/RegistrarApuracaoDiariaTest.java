package br.com.luismarangoni.jornada_api.jornada.aplicacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import br.com.luismarangoni.jornada_api.jornada.ApuracaoDiaria;
import br.com.luismarangoni.jornada_api.jornada.SituacaoApuracao;
import br.com.luismarangoni.jornada_api.jornada.VerificarJornadaDiaria;
import br.com.luismarangoni.jornada_api.jornada.aplicacao.porta.ApuracaoDiariaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrarApuracaoDiariaTest {

    private VerificarJornadaDiaria verificar;
    private ApuracaoDiariaRepository repository;
    private RegistrarApuracaoDiaria registrar;

    private final LocalDate data =
            LocalDate.of(2026, 9, 17);

    @BeforeEach
    void configurar() {
        verificar = mock(VerificarJornadaDiaria.class);
        repository = mock(ApuracaoDiariaRepository.class);

        registrar = new RegistrarApuracaoDiaria(
                verificar,
                repository
        );
    }

    @Test
    void deveSalvarQuandoAindaNaoExisteApuracao() {
        var apuracao = new ApuracaoDiaria(
                1L, data, 510, 510,
                0, false, SituacaoApuracao.REGULAR
        );

        when(repository.buscarPorFuncionarioEData(1L, data))
                .thenReturn(Optional.empty());
        when(verificar.executar(1L, data))
                .thenReturn(apuracao);
        when(repository.salvar(apuracao))
                .thenReturn(apuracao);

        var resultado = registrar.executar(1L, data);

        assertThat(resultado).isEqualTo(apuracao);
        verify(repository).salvar(apuracao);
    }

    @Test
    void naoDeveSalvarNovamenteQuandoApuracaoJaExiste() {
        var existente = new ApuracaoDiaria(
                1L, data, 510, 490,
                10, false, SituacaoApuracao.DEFICIT
        );

        when(repository.buscarPorFuncionarioEData(1L, data))
                .thenReturn(Optional.of(existente));

        var resultado = registrar.executar(1L, data);

        assertThat(resultado).isEqualTo(existente);
        verify(repository, never()).salvar(any());
        verifyNoInteractions(verificar);
    }
}

package br.com.luismarangoni.jornada_api.jornada.infra.persistencia;

import br.com.luismarangoni.jornada_api.jornada.SituacaoApuracao;
import jakarta.persistence.*;
import br.com.luismarangoni.jornada_api.jornada.ApuracaoDiaria;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "apuracoes_diarias",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_apuracao_funcionario_data",
                columnNames = {"funcionario_id", "data"}
        )
)
public class ApuracaoDiariaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "minutos_previstos", nullable = false)
    private int minutosPrevistos;

    @Column(name = "minutos_trabalhados", nullable = false)
    private int minutosTrabalhados;

    @Column(name = "minutos_deficit", nullable = false)
    private int minutosDeficit;

    @Column(nullable = false)
    private boolean ausente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoApuracao situacao;

    @Column(name = "criada_em", nullable = false)
    private OffsetDateTime criadaEm;

    protected ApuracaoDiariaJpaEntity() {
    }

    public ApuracaoDiariaJpaEntity(ApuracaoDiaria apuracao) {
        this.funcionarioId = apuracao.funcionarioId();
        this.data = apuracao.data();
        this.minutosPrevistos = apuracao.minutosPrevistos();
        this.minutosTrabalhados = apuracao.minutosTrabalhados();
        this.minutosDeficit = apuracao.minutosDeficit();
        this.ausente = apuracao.ausente();
        this.situacao = apuracao.situacao();
        this.criadaEm = java.time.OffsetDateTime.now(ZoneOffset.UTC);
    }

    public ApuracaoDiaria toDomain() {
        return new ApuracaoDiaria(
                funcionarioId,
                data,
                minutosPrevistos,
                minutosTrabalhados,
                minutosDeficit,
                ausente,
                situacao
        );
    }
}

package br.com.luismarangoni.jornada_api.marcacao.infra.persistencia;

import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "marcacoes_ponto")
public class MarcacaoPontoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMarcacao tipo;

    @Column(name = "ocorrida_em", nullable = false)
    private Instant ocorridaEm;

    protected MarcacaoPontoJpaEntity() {
    }

    public MarcacaoPontoJpaEntity(
            Long funcionarioId,
            TipoMarcacao tipo,
            Instant ocorridaEm
    ) {
        this.funcionarioId = funcionarioId;
        this.tipo = tipo;
        this.ocorridaEm = ocorridaEm;
    }

    public Long getId() {
        return id;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public TipoMarcacao getTipo() {
        return tipo;
    }

    public Instant getOcorridaEm() {
        return ocorridaEm;
    }
}

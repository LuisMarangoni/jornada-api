package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionarios")
public class FuncionarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false)
    private boolean ativo;

    protected FuncionarioJpaEntity() {
    }

    public void atualizarDados(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public FuncionarioJpaEntity(
            String matricula,
            String nome,
            String email,
            boolean ativo
    ) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAtivo() {
        return ativo;
    }
}

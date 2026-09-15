package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.MatriculaJaCadastradaException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class FuncionarioRepositoryAdapter
        implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;

    public FuncionarioRepositoryAdapter(
            FuncionarioJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existePorMatricula(String matricula) {
        return jpaRepository.existsByMatricula(matricula);
    }

    @Override
    public Long salvar(Funcionario funcionario) {
        FuncionarioJpaEntity entidade = new FuncionarioJpaEntity(
                funcionario.getMatricula(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.isAtivo()
        );

        try {
            return jpaRepository.saveAndFlush(entidade).getId();
        } catch (DataIntegrityViolationException exception) {
            if (violouMatriculaUnica(exception)) {
                throw new MatriculaJaCadastradaException(
                        funcionario.getMatricula(),
                        exception
                );
            }

            throw exception;
        }
    }

    private boolean violouMatriculaUnica(Throwable erro) {
        Throwable causa = erro;

        while (causa != null) {
            if (causa instanceof ConstraintViolationException violacao
                    && "uk_funcionarios_matricula".equals(
                    violacao.getConstraintName()
            )) {
                return true;
            }

            causa = causa.getCause();
        }

        return false;
    }
}

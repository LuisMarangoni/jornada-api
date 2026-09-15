package br.com.luismarangoni.jornada_api.funcionario.infra.persistencia;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.PaginaFuncionarios;
import org.springframework.data.domain.PageRequest;
import br.com.luismarangoni.jornada_api.funcionario.Funcionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.MatriculaJaCadastradaException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioConsulta;
import java.util.Optional;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioNaoEncontradoException;

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

    @Override
    public Optional<FuncionarioConsulta> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(entidade -> new FuncionarioConsulta(
                        entidade.getId(),
                        entidade.getMatricula(),
                        entidade.getNome(),
                        entidade.getEmail(),
                        entidade.isAtivo()
                ));
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

    @Override
    public PaginaFuncionarios listar(int pagina, int tamanho) {
        if (pagina < 0) {
            throw new IllegalArgumentException(
                    "A página não pode ser negativa"
            );
        }

        if (tamanho < 1 || tamanho > 100) {
            throw new IllegalArgumentException(
                    "O tamanho deve estar entre 1 e 100"
            );
        }

        var resultado = jpaRepository.findAllByOrderByIdAsc(
                PageRequest.of(pagina, tamanho)
        );

        var conteudo = resultado.getContent()
                .stream()
                .map(entidade -> new FuncionarioConsulta(
                        entidade.getId(),
                        entidade.getMatricula(),
                        entidade.getNome(),
                        entidade.getEmail(),
                        entidade.isAtivo()
                ))
                .toList();

        return new PaginaFuncionarios(
                conteudo,
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Override
    public void atualizar(Long id, String nome, String email) {
        FuncionarioJpaEntity entidade = jpaRepository.findById(id)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(id));

        entidade.atualizarDados(nome, email);
        jpaRepository.saveAndFlush(entidade);
    }

    @Override
    public void atualizarStatus(Long id, boolean ativo) {
        FuncionarioJpaEntity entidade = jpaRepository.findById(id)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(id));

        entidade.alterarStatus(ativo);
        jpaRepository.saveAndFlush(entidade);
    }

}

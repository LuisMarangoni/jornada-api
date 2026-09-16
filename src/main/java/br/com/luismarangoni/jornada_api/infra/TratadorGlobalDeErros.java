package br.com.luismarangoni.jornada_api.infra;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.MatriculaJaCadastradaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioNaoEncontradoException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.FuncionarioInativoException;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.SequenciaMarcacaoInvalidaException;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.UsuarioJaVinculadoException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class TratadorGlobalDeErros extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MatriculaJaCadastradaException.class)
    public ProblemDetail tratarMatriculaDuplicada(
            MatriculaJaCadastradaException exception
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problema.setTitle("Matrícula já cadastrada");

        return problema;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Confira os campos informados"
        );

        problema.setTitle("Dados inválidos");

        Map<String, List<String>> erros = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(erro -> {
                    erros.computeIfAbsent(
                            erro.getField(),
                            campo -> new ArrayList<>()
                    ).add(erro.getDefaultMessage());
                });

        problema.setProperty("erros", erros);

        return handleExceptionInternal(
                exception,
                problema,
                headers,
                status,
                request
        );
    }

    @ExceptionHandler(FuncionarioNaoEncontradoException.class)
    public ProblemDetail tratarFuncionarioNaoEncontrado(
            FuncionarioNaoEncontradoException exception
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problema.setTitle("Funcionário não encontrado");

        return problema;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail tratarArgumentoInvalido(
            IllegalArgumentException exception
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );

        problema.setTitle("Parâmetro inválido");

        return problema;
    }

    @ExceptionHandler(FuncionarioInativoException.class)
    public ProblemDetail tratarFuncionarioInativo(
            FuncionarioInativoException exception
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage()
        );

        problema.setTitle("Funcionário inativo");

        return problema;
    }

    @ExceptionHandler(SequenciaMarcacaoInvalidaException.class)
    public ProblemDetail tratarSequenciaInvalida(
            SequenciaMarcacaoInvalidaException exception
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage()
        );

        problema.setTitle("Sequência de marcação inválida");

        return problema;
    }

    @ExceptionHandler(UsuarioJaVinculadoException.class)
    public ProblemDetail tratarUsuarioJaVinculado(
            UsuarioJaVinculadoException exception
    ) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problema.setTitle("Usuário já vinculado");

        return problema;
    }

}

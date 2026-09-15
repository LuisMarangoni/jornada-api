package br.com.luismarangoni.jornada_api.funcionario.api;

import br.com.luismarangoni.jornada_api.funcionario.api.dto.CadastrarFuncionarioRequest;
import br.com.luismarangoni.jornada_api.funcionario.api.dto.FuncionarioCriadoResponse;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.CadastrarFuncionario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final CadastrarFuncionario cadastrarFuncionario;

    public FuncionarioController(
            CadastrarFuncionario cadastrarFuncionario
    ) {
        this.cadastrarFuncionario = cadastrarFuncionario;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioCriadoResponse cadastrar(
            @Valid @RequestBody CadastrarFuncionarioRequest request
    ) {
        Long id = cadastrarFuncionario.executar(
                request.matricula(),
                request.nome(),
                request.email()
        );

        return new FuncionarioCriadoResponse(id);
    }
}

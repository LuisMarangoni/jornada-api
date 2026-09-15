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
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.BuscarFuncionario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import br.com.luismarangoni.jornada_api.funcionario.api.dto.FuncionarioResponse;
import br.com.luismarangoni.jornada_api.funcionario.api.dto.PaginaFuncionariosResponse;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.ListarFuncionarios;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final CadastrarFuncionario cadastrarFuncionario;
    private final BuscarFuncionario buscarFuncionario;
    private final ListarFuncionarios listarFuncionarios;

    public FuncionarioController(
            CadastrarFuncionario cadastrarFuncionario,
            BuscarFuncionario buscarFuncionario,
            ListarFuncionarios listarFuncionarios
    ) {
        this.cadastrarFuncionario = cadastrarFuncionario;
        this.buscarFuncionario = buscarFuncionario;
        this.listarFuncionarios = listarFuncionarios;
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

    @GetMapping("/{id}")
    public FuncionarioResponse buscar(@PathVariable Long id) {
        return FuncionarioResponse.from(
                buscarFuncionario.executar(id)
        );
    }

    @GetMapping
    public PaginaFuncionariosResponse listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho
    ) {
        return PaginaFuncionariosResponse.from(
                listarFuncionarios.executar(pagina, tamanho)
        );
    }

}

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
import br.com.luismarangoni.jornada_api.funcionario.api.dto.AtualizarFuncionarioRequest;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.AtualizarFuncionario;
import org.springframework.web.bind.annotation.PutMapping;
import br.com.luismarangoni.jornada_api.funcionario.api.dto.AlterarStatusFuncionarioRequest;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.AlterarStatusFuncionario;
import org.springframework.web.bind.annotation.PatchMapping;
import br.com.luismarangoni.jornada_api.marcacao.api.dto.MarcacaoResponse;
import br.com.luismarangoni.jornada_api.marcacao.api.dto.RegistrarMarcacaoRequest;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.RegistrarMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.ListarMarcacoesFuncionario;

import java.util.List;




@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final CadastrarFuncionario cadastrarFuncionario;
    private final BuscarFuncionario buscarFuncionario;
    private final ListarFuncionarios listarFuncionarios;
    private final AtualizarFuncionario atualizarFuncionario;
    private final AlterarStatusFuncionario alterarStatusFuncionario;
    private final RegistrarMarcacao registrarMarcacao;
    private final ListarMarcacoesFuncionario listarMarcacoesFuncionario;

    public FuncionarioController(
            CadastrarFuncionario cadastrarFuncionario,
            BuscarFuncionario buscarFuncionario,
            ListarFuncionarios listarFuncionarios,
            AtualizarFuncionario atualizarFuncionario,
            AlterarStatusFuncionario alterarStatusFuncionario,
            RegistrarMarcacao registrarMarcacao,
            ListarMarcacoesFuncionario listarMarcacoesFuncionario
    ) {
        this.cadastrarFuncionario = cadastrarFuncionario;
        this.buscarFuncionario = buscarFuncionario;
        this.listarFuncionarios = listarFuncionarios;
        this.atualizarFuncionario = atualizarFuncionario;
        this.alterarStatusFuncionario = alterarStatusFuncionario;
        this.registrarMarcacao = registrarMarcacao;
        this.listarMarcacoesFuncionario = listarMarcacoesFuncionario;
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

    @PutMapping("/{id}")
    public FuncionarioResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarFuncionarioRequest request
    ) {
        return FuncionarioResponse.from(
                atualizarFuncionario.executar(
                        id,
                        request.nome(),
                        request.email()
                )
        );
    }

    @GetMapping("/{funcionarioId}/marcacoes")
    public List<MarcacaoResponse> listarMarcacoes(
            @PathVariable Long funcionarioId
    ) {
        return listarMarcacoesFuncionario.executar(funcionarioId)
                .stream()
                .map(MarcacaoResponse::from)
                .toList();
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

    @PatchMapping("/{id}/ativo")
    public FuncionarioResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AlterarStatusFuncionarioRequest request
    ) {
        return FuncionarioResponse.from(
                alterarStatusFuncionario.executar(id, request.ativo())
        );
    }

    @PostMapping("/{funcionarioId}/marcacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public MarcacaoResponse registrarMarcacao(
            @PathVariable Long funcionarioId,
            @Valid @RequestBody RegistrarMarcacaoRequest request
    ) {
        return MarcacaoResponse.from(
                registrarMarcacao.executar(
                        funcionarioId,
                        request.tipo()
                )
        );
    }

}

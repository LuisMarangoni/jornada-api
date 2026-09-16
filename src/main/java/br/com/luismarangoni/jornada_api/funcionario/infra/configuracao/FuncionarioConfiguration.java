package br.com.luismarangoni.jornada_api.funcionario.infra.configuracao;


import br.com.luismarangoni.jornada_api.funcionario.aplicacao.AtualizarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.BuscarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.CadastrarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.ListarFuncionarios;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.AlterarStatusFuncionario;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.RegistrarMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.ListarMarcacoesFuncionario;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.RegistrarMarcacao;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta.MarcacaoPontoRepository;
import java.time.Clock;
import br.com.luismarangoni.jornada_api.marcacao.apuracao.ApuradorJornada;
import br.com.luismarangoni.jornada_api.marcacao.apuracao.ApurarJornadaFuncionario;

@Configuration(proxyBeanMethods = false)
public class FuncionarioConfiguration {

    @Bean
    public CadastrarFuncionario cadastrarFuncionario(
            FuncionarioRepository repository
    ) {
        return new CadastrarFuncionario(repository);
    }

    @Bean
    public BuscarFuncionario buscarFuncionario(
            FuncionarioRepository repository
    ) {
        return new BuscarFuncionario(repository);
    }

    @Bean
    public ListarFuncionarios listarFuncionarios(
            FuncionarioRepository repository
    ) {
        return new ListarFuncionarios(repository);
    }

    @Bean
    public AtualizarFuncionario atualizarFuncionario(
            FuncionarioRepository repository
    ) {
        return new AtualizarFuncionario(repository);
    }

    @Bean
    public AlterarStatusFuncionario alterarStatusFuncionario(
            FuncionarioRepository repository
    ) {
        return new AlterarStatusFuncionario(repository);
    }

    @Bean
    public RegistrarMarcacao registrarMarcacao(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository
    ) {
        return new RegistrarMarcacao(
                funcionarioRepository,
                marcacaoRepository,
                Clock.systemUTC()
        );
    }

    @Bean
    public ListarMarcacoesFuncionario listarMarcacoesFuncionario(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository
    ) {
        return new ListarMarcacoesFuncionario(
                funcionarioRepository,
                marcacaoRepository
        );
    }

    @Bean
    public ApuradorJornada apuradorJornada() {
        return new ApuradorJornada();
    }

    @Bean
    public ApurarJornadaFuncionario apurarJornadaFuncionario(
            FuncionarioRepository funcionarioRepository,
            MarcacaoPontoRepository marcacaoRepository,
            ApuradorJornada apurador
    ) {
        return new ApurarJornadaFuncionario(
                funcionarioRepository,
                marcacaoRepository,
                apurador
        );
    }

}

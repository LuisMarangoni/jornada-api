package br.com.luismarangoni.jornada_api.funcionario.infra.configuracao;


import br.com.luismarangoni.jornada_api.funcionario.aplicacao.AtualizarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.BuscarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.CadastrarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.ListarFuncionarios;




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

}

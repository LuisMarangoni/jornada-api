package br.com.luismarangoni.jornada_api.funcionario.infra.configuracao;



import br.com.luismarangoni.jornada_api.funcionario.aplicacao.BuscarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.CadastrarFuncionario;
import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;





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
}

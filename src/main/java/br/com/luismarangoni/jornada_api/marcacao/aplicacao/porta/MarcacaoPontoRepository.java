package br.com.luismarangoni.jornada_api.marcacao.aplicacao.porta;

import br.com.luismarangoni.jornada_api.marcacao.MarcacaoPonto;
import br.com.luismarangoni.jornada_api.marcacao.aplicacao.MarcacaoConsulta;

import java.util.List;

public interface MarcacaoPontoRepository {

    Long salvar(MarcacaoPonto marcacao);

    List<MarcacaoConsulta> listarPorFuncionario(Long funcionarioId);
}

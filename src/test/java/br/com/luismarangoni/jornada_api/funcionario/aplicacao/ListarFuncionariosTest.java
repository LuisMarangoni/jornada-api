package br.com.luismarangoni.jornada_api.funcionario.aplicacao;

import br.com.luismarangoni.jornada_api.funcionario.aplicacao.porta.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarFuncionariosTest {

    @Mock
    private FuncionarioRepository repository;

    @Test
    void deveListarFuncionariosComPaginaInformada() {
        PaginaFuncionarios paginaEsperada =
                new PaginaFuncionarios(
                        List.of(
                                new FuncionarioConsulta(
                                        1L,
                                        "MAT-001",
                                        "Ana Silva",
                                        "ana@email.com",
                                        true
                                )
                        ),
                        0,
                        10,
                        1,
                        1
                );

        when(repository.listar(0, 10))
                .thenReturn(paginaEsperada);

        ListarFuncionarios listarFuncionarios =
                new ListarFuncionarios(repository);

        PaginaFuncionarios resultado =
                listarFuncionarios.executar(0, 10);

        assertEquals(paginaEsperada, resultado);
        verify(repository).listar(0, 10);
    }

    @Test
    void devePropagarPaginaVazia() {
        PaginaFuncionarios paginaVazia =
                new PaginaFuncionarios(
                        List.of(),
                        2,
                        10,
                        0,
                        0
                );

        when(repository.listar(2, 10))
                .thenReturn(paginaVazia);

        ListarFuncionarios listarFuncionarios =
                new ListarFuncionarios(repository);

        PaginaFuncionarios resultado =
                listarFuncionarios.executar(2, 10);

        assertTrue(resultado.conteudo().isEmpty());
        assertEquals(2, resultado.pagina());
        assertEquals(10, resultado.tamanho());
        verify(repository).listar(2, 10);
    }
}

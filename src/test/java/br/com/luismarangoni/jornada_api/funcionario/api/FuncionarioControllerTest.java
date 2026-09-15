package br.com.luismarangoni.jornada_api.funcionario.api;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import br.com.luismarangoni.jornada_api.funcionario.infra.persistencia.FuncionarioJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestConfiguration.class)
@Transactional
class FuncionarioControllerTest {

    private static final String CADASTRO_VALIDO = """
            {
              "matricula": "MAT-201",
              "nome": "Ana Silva",
              "email": "ana@email.com"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FuncionarioJpaRepository repository;

    @Test
    void deveCadastrarFuncionarioERetornar201() throws Exception {
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CADASTRO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").isNumber());

        assertTrue(repository.existsByMatricula("MAT-201"));
    }

    @Test
    void deveRejeitarEmailInvalidoCom400() throws Exception {
        String corpo = """
                {
                  "matricula": "MAT-202",
                  "nome": "Ana Silva",
                  "email": "email-invalido"
                }
                """;

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.erros.email").isArray());

        assertFalse(repository.existsByMatricula("MAT-202"));
    }

    @Test
    void deveRejeitarMatriculaDuplicadaCom409() throws Exception {
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CADASTRO_VALIDO))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CADASTRO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title")
                        .value("Matrícula já cadastrada"))
                .andExpect(jsonPath("$.detail")
                        .value("Matrícula já cadastrada: MAT-201"));
    }

    @Test
    void deveBuscarFuncionarioPorId() throws Exception {
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "matricula": "MAT-301",
                              "nome": "Carlos Souza",
                              "email": "carlos@email.com"
                            }
                            """))
                .andExpect(status().isCreated());

        Long id = repository.findByMatricula("MAT-301")
                .orElseThrow()
                .getId();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request
                                .MockMvcRequestBuilders
                                .get("/funcionarios/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.matricula").value("MAT-301"))
                .andExpect(jsonPath("$.nome").value("Carlos Souza"))
                .andExpect(jsonPath("$.email").value("carlos@email.com"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar404QuandoFuncionarioNaoExistir() throws Exception {
        mockMvc.perform(
                        org.springframework.test.web.servlet.request
                                .MockMvcRequestBuilders
                                .get("/funcionarios/{id}", 999999L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("Funcionário não encontrado"))
                .andExpect(jsonPath("$.detail")
                        .value("Funcionário não encontrado: 999999"));
    }

    @Test
    void deveListarFuncionariosComPaginacao() throws Exception {
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "matricula": "MAT-401",
                              "nome": "Ana Silva",
                              "email": "ana401@email.com"
                            }
                            """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "matricula": "MAT-402",
                              "nome": "Bruno Souza",
                              "email": "bruno402@email.com"
                            }
                            """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/funcionarios")
                        .param("pagina", "0")
                        .param("tamanho", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isArray())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.conteudo[0].matricula")
                        .value("MAT-401"))
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(1))
                .andExpect(jsonPath("$.totalElementos").value(2))
                .andExpect(jsonPath("$.totalPaginas").value(2));
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHouverMaisRegistros()
            throws Exception {
        mockMvc.perform(get("/funcionarios")
                        .param("pagina", "5")
                        .param("tamanho", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isArray())
                .andExpect(jsonPath("$.conteudo").isEmpty())
                .andExpect(jsonPath("$.pagina").value(5))
                .andExpect(jsonPath("$.tamanho").value(10))
                .andExpect(jsonPath("$.totalElementos").value(0))
                .andExpect(jsonPath("$.totalPaginas").value(0));
    }

    @Test
    void deveRejeitarPaginaNegativa() throws Exception {
        mockMvc.perform(get("/funcionarios")
                        .param("pagina", "-1")
                        .param("tamanho", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"));
    }

    @Test
    void deveRejeitarTamanhoForaDoLimite() throws Exception {
        mockMvc.perform(get("/funcionarios")
                        .param("pagina", "0")
                        .param("tamanho", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"));
    }

    @Test
    void deveAtualizarFuncionario() throws Exception {
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "matricula": "MAT-501",
                              "nome": "Ana Silva",
                              "email": "ana@email.com"
                            }
                            """))
                .andExpect(status().isCreated());

        Long id = repository.findByMatricula("MAT-501")
                .orElseThrow()
                .getId();

        mockMvc.perform(put("/funcionarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "Bruno Souza",
                              "email": "BRUNO@EMAIL.COM"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.matricula").value("MAT-501"))
                .andExpect(jsonPath("$.nome").value("Bruno Souza"))
                .andExpect(jsonPath("$.email").value("bruno@email.com"))
                .andExpect(jsonPath("$.ativo").value(true));

        var salvo = repository.findById(id).orElseThrow();
        assertEquals("Bruno Souza", salvo.getNome());
        assertEquals("bruno@email.com", salvo.getEmail());
    }

    @Test
    void deveRetornar404AoAtualizarFuncionarioInexistente()
            throws Exception {
        mockMvc.perform(put("/funcionarios/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "Bruno Souza",
                              "email": "bruno@email.com"
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("Funcionário não encontrado"));
    }

    @Test
    void deveRejeitarDadosInvalidosNaAtualizacao() throws Exception {
        mockMvc.perform(put("/funcionarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "",
                              "email": "email-invalido"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.erros.nome").isArray())
                .andExpect(jsonPath("$.erros.email").isArray());
    }

}

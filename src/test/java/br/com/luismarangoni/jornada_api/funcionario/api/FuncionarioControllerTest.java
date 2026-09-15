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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

}

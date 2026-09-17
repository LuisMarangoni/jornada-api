package br.com.luismarangoni.jornada_api.infra;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import br.com.luismarangoni.jornada_api.funcionario.infra.persistencia.FuncionarioJpaEntity;
import br.com.luismarangoni.jornada_api.funcionario.infra.persistencia.FuncionarioJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestConfiguration.class)
class SegurancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FuncionarioJpaRepository funcionarioRepository;

    @Test
    void deveNegarRotaProtegidaSemToken() throws Exception {
        mockMvc.perform(get("/funcionarios/999999"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirRotaProtegidaComJwtValido() throws Exception {
        mockMvc.perform(
                        get("/funcionarios/999999")
                                .with(jwt()
                                        .jwt(token -> token
                                                .subject("1")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of(
                                                                "USUARIO"
                                                        )
                                                )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarAlteracaoAdministrativaParaUsuarioComum()
            throws Exception {
        mockMvc.perform(
                        patch("/funcionarios/{id}/ativo", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "ativo": false
                                    }
                                    """)
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_USUARIO")
                                        )
                                        .jwt(token -> token
                                                .subject("1")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of("USUARIO")
                                                )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirConsultaParaUsuarioAutenticado()
            throws Exception {
        mockMvc.perform(
                        get("/funcionarios/999999")
                                .with(jwt()
                                        .jwt(token -> token
                                                .subject("1")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of(
                                                                "USUARIO"
                                                        )
                                                )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirOperacaoAdministrativaParaSuporte()
            throws Exception {
        mockMvc.perform(
                        patch("/funcionarios/{id}/ativo", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "ativo": false
                                    }
                                    """)
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_SUPORTE")
                                        )
                                        .jwt(token -> token
                                                .subject("2")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of("SUPORTE")
                                                )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveNegarVinculoParaUsuarioComum()
            throws Exception {
        mockMvc.perform(
                        patch("/funcionarios/{id}/usuario", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "usuarioId": 501
                                    }
                                    """)
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USUARIO"
                                                )
                                        )
                                        .jwt(token -> token
                                                .subject("1")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of(
                                                                "USUARIO"
                                                        )
                                                )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirVinculoParaSuporte()
            throws Exception {
        mockMvc.perform(
                        patch("/funcionarios/{id}/usuario", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "usuarioId": 501
                                    }
                                    """)
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_SUPORTE"
                                                )
                                        )
                                        .jwt(token -> token
                                                .subject("2")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of(
                                                                "SUPORTE"
                                                        )
                                                )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void devePermitirUsuarioConsultarProprioFuncionario()
            throws Exception {
        var funcionario = funcionarioRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-1501",
                        "Ana Silva",
                        "ana1501@email.com",
                        true,
                        801L
                )
        );

        mockMvc.perform(
                        get("/funcionarios/{id}", funcionario.getId())
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USUARIO"
                                                )
                                        )
                                        .jwt(token -> token
                                                .subject("801")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of(
                                                                "USUARIO"
                                                        )
                                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(funcionario.getId()));
    }

    @Test
    void deveNegarConsultaParaUsuarioSemVinculo()
            throws Exception {
        var funcionario = funcionarioRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-1502",
                        "Bruno Souza",
                        "bruno1502@email.com",
                        true,
                        802L
                )
        );

        mockMvc.perform(
                        get("/funcionarios/{id}", funcionario.getId())
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_USUARIO")
                                        )
                                        .jwt(token -> token
                                                .subject("1")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of("USUARIO")
                                                )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarListagemGeralParaUsuarioComum()
            throws Exception {
        mockMvc.perform(
                        get("/funcionarios")
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USUARIO"
                                                )
                                        )
                                        .jwt(token -> token
                                                .subject("801")
                                                .claim(
                                                        "perfis",
                                                        java.util.List.of(
                                                                "USUARIO"
                                                        )
                                                )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarRegistroDeMarcacaoEmOutroFuncionario()
            throws Exception {
        var funcionario = funcionarioRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-1601",
                        "Bruno Souza",
                        "bruno1601@email.com",
                        true,
                        902L
                )
        );

        mockMvc.perform(
                        post(
                                "/funcionarios/{id}/marcacoes",
                                funcionario.getId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "tipo": "ENTRADA"
                                    }
                                    """)
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USUARIO"
                                                )
                                        )
                                        .jwt(token -> token.subject("901")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarConsultaDeMarcacoesDeOutroFuncionario()
            throws Exception {
        var funcionario = funcionarioRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-1602",
                        "Carla Lima",
                        "carla1602@email.com",
                        true,
                        902L
                )
        );

        mockMvc.perform(
                        get(
                                "/funcionarios/{id}/marcacoes",
                                funcionario.getId()
                        )
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USUARIO"
                                                )
                                        )
                                        .jwt(token -> token.subject("901")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarResumoDeOutroFuncionario()
            throws Exception {
        var funcionario = funcionarioRepository.saveAndFlush(
                new FuncionarioJpaEntity(
                        "MAT-1603",
                        "Diego Alves",
                        "diego1603@email.com",
                        true,
                        902L
                )
        );

        mockMvc.perform(
                        get(
                                "/funcionarios/{id}/jornada/resumo",
                                funcionario.getId()
                        )
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USUARIO"
                                                )
                                        )
                                        .jwt(token -> token.subject("901")))
                )
                .andExpect(status().isForbidden());
    }
}

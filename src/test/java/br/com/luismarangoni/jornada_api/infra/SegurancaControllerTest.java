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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestConfiguration.class)
class SegurancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(status().isNotFound());
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
                .andExpect(status().isNotFound());
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
}

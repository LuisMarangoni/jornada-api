package br.com.luismarangoni.jornada_api.infra;

import br.com.luismarangoni.jornada_api.PostgresTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

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
}

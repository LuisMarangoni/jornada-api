package br.com.luismarangoni.jornada_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
class JornadaApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
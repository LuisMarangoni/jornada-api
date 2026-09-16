package br.com.luismarangoni.jornada_api.funcionario.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VincularUsuarioRequest(

        @NotNull(message = "O usuário é obrigatório")
        @Positive(message = "O usuário deve possuir um ID positivo")
        Long usuarioId

) {
}

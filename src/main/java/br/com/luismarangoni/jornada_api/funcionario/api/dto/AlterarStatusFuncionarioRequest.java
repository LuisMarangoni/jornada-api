package br.com.luismarangoni.jornada_api.funcionario.api.dto;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusFuncionarioRequest(

        @NotNull(message = "O status ativo é obrigatório")
        Boolean ativo

) {
}

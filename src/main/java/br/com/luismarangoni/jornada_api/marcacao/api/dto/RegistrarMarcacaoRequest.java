package br.com.luismarangoni.jornada_api.marcacao.api.dto;

import br.com.luismarangoni.jornada_api.marcacao.TipoMarcacao;
import jakarta.validation.constraints.NotNull;

public record RegistrarMarcacaoRequest(

        @NotNull(message = "O tipo da marcação é obrigatório")
        TipoMarcacao tipo

) {
}

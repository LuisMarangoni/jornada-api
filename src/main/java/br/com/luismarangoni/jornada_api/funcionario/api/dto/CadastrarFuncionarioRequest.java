package br.com.luismarangoni.jornada_api.funcionario.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastrarFuncionarioRequest(

        @NotBlank(message = "A matrícula é obrigatória")
        @Size(max = 50, message = "A matrícula deve ter até 50 caracteres")
        String matricula,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter até 150 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve possuir um formato válido")
        @Size(max = 254, message = "O e-mail deve ter até 254 caracteres")
        String email

) {
}

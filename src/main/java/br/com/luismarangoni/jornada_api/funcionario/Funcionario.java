package br.com.luismarangoni.jornada_api.funcionario;

import java.util.Locale;

public class Funcionario {

    private final String matricula;
    private final String nome;
    private final String email;
    private final boolean ativo;

    public Funcionario(
            String matricula,
            String nome,
            String email
    ) {
        this.matricula = exigirTexto(matricula, "Matrícula");
        this.nome = exigirTexto(nome, "Nome");
        this.email = exigirTexto(email, "E-mail")
                .toLowerCase(Locale.ROOT);
        this.ativo = true;
    }

    private static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " é obrigatório"
            );
        }

        return valor.trim();
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
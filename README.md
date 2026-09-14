# Jornada API

Projeto de portfólio em desenvolvimento para gestão de funcionários e jornadas de trabalho, construído incrementalmente com Java e Spring Boot.

## Estado atual

- Estrutura inicial com Spring Boot 4.1.1 e Java 21.
- Porta HTTP configurada como `8082`, sobrescrevível por `SERVER_PORT`.
- Classe de domínio `Funcionario`, com matrícula, nome, e-mail e estado ativo.
- Rejeição de campos obrigatórios nulos, vazios ou contendo apenas espaços.
- Remoção de espaços nas extremidades e normalização do e-mail para minúsculas.
- Funcionários são criados ativos.
- 11 testes: 10 cenários do domínio e 1 de carregamento do contexto Spring.

Ainda não existem endpoints de negócio, persistência, autenticação ou registro de ponto. A classe atual não verifica formato de e-mail nem unicidade da matrícula; essas validações serão implementadas nas próximas etapas.

## Pré-requisitos

- JDK 21.
- Acesso à internet para baixar dependências na primeira execução.

O projeto inclui Maven Wrapper. Nesta etapa, não é necessário instalar PostgreSQL, iniciar Docker ou executar os projetos anteriores.

## Testes

Na raiz do projeto, no PowerShell:

```powershell
.\mvnw.cmd test
```

Os testes de `Funcionario` instanciam a classe diretamente, sem carregar o Spring. O teste de contexto verifica a configuração inicial da aplicação.

## Executar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação inicia na porta `8082`. Como ainda não há controllers de negócio, acessar `/` pode retornar `404`; isso não indica falha de inicialização. Para encerrar, pressione `Ctrl+C` no terminal.

## Organização atual

```text
src/
├── main/
│   ├── java/br/com/luismarangoni/jornada_api/
│   │   ├── JornadaApiApplication.java
│   │   └── funcionario/Funcionario.java
│   └── resources/application.properties
└── test/
    └── java/br/com/luismarangoni/jornada_api/
        ├── JornadaApiApplicationTests.java
        └── funcionario/FuncionarioTest.java
```

## Roadmap

1. **Base do domínio:** estrutura inicial e testes de funcionário — concluída.
2. **Cadastro persistente:** PostgreSQL, migrations Flyway, API de funcionários e jornada prevista.
3. **Controle de acesso:** contas e permissões de funcionário/RH, separadas dos dados profissionais.
4. **Marcações e apuração:** entradas, saídas, intervalos e identificação de pendências.
5. **Ajustes auditáveis:** solicitações, aprovação pelo RH e preservação do histórico original.
6. **Notificações:** e-mails de pendências, inicialmente capturados em ambiente local.
7. **Integração Python:** automações e relatórios consumindo a API, sem escrita direta no banco nem duplicação das regras de jornada.
8. **Interface:** marcação, consulta pessoal e painel do RH.

Cada etapa deve ser acompanhada de testes e documentação. As funcionalidades planejadas não estão disponíveis na versão atual.

## Escopo

Este é um projeto educacional com dados fictícios, não um registrador oficial de ponto nem uma aplicação pronta para produção. Eventuais cálculos financeiros serão simulações sujeitas à conferência, sem desconto automático em folha. Adequação para uso trabalhista real está fora do escopo desta etapa.

# Jornada API

[![CI](https://github.com/LuisMarangoni/jornada-api/actions/workflows/ci.yml/badge.svg)](https://github.com/LuisMarangoni/jornada-api/actions/workflows/ci.yml)

Projeto de portfólio em desenvolvimento para gestão de funcionários e jornadas de trabalho, construído incrementalmente com Java e Spring Boot.

## Estado atual

- Estrutura inicial com Spring Boot 4.1.1 e Java 21.
- Porta HTTP configurada como `8082`, sobrescrevível por `SERVER_PORT`.
- Classe de domínio `Funcionario`, com matrícula, nome, e-mail e estado ativo.
- Rejeição de campos obrigatórios nulos, vazios ou contendo apenas espaços.
- Remoção de espaços nas extremidades e normalização do e-mail para minúsculas.
- Funcionários são criados ativos.
- Persistência com Spring Data JPA e PostgreSQL 17, separada do domínio.
- Migration Flyway para a tabela `funcionarios`, com matrícula única.
- 13 testes: 10 cenários do domínio, 2 de persistência e 1 de contexto Spring.
- GitHub Actions executa a suíte com Java 21 em pushes e pull requests para `main`.

Ainda não existem casos de uso de cadastro expostos por endpoints, autenticação ou registro de ponto. O domínio ainda não verifica formato de e-mail ou limites de tamanho. A unicidade da matrícula já é garantida no PostgreSQL; o tratamento dessa violação na API será implementado posteriormente.

## Pré-requisitos

- JDK 21.
- Docker Desktop com containers Linux, para desenvolvimento e testes de integração.
- Acesso à internet para baixar dependências na primeira execução.

O projeto inclui Maven Wrapper. Não é necessário instalar PostgreSQL diretamente no sistema nem executar os projetos anteriores.

## Testes

Com Docker disponível, na raiz do projeto, no PowerShell:

```powershell
.\mvnw.cmd test
```

Os testes de `Funcionario` instanciam a classe diretamente, sem carregar o Spring. Para executar apenas esses testes, sem Docker:

```powershell
.\mvnw.cmd "-Dtest=FuncionarioTest" test
```

Os testes de contexto e persistência utilizam Testcontainers com PostgreSQL 17 em um container temporário, com porta dinâmica e credenciais fictícias. `@ServiceConnection` fornece a conexão ao Spring; não é necessário `.env`, senha real, API em execução ou banco do Compose. O arquivo `src/test/resources/application.properties` mantém a configuração dos testes separada da configuração local.

O Flyway executa as migrations nesse banco temporário e o Hibernate valida o mapeamento. Os testes de repositório verificam gravação e leitura após limpar o contexto de persistência, além da rejeição de matrícula duplicada. As transações dos testes são revertidas ao final. Uma mensagem SQL de chave duplicada é esperada no cenário que provoca essa violação; a suíte deve terminar com sucesso.

O Spring gerencia o ciclo de vida do container de teste. Os dados do volume de desenvolvimento não são utilizados. A execução completa no GitHub Actions também depende do Docker disponível no runner.

## Banco de desenvolvimento

Crie `.env` na raiz, seguindo `.env.example`, e substitua o exemplo pela senha local:

```dotenv
JORNADA_DB_PASSWORD=defina_uma_senha_local
```

Não versione `.env` nem inclua credenciais reais no `.env.example`. O mesmo arquivo é lido pelo Compose e pelo Spring como Java Properties; esses formatos têm diferenças em aspas, barras invertidas e interpolação. Não presuma interpretação idêntica desses caracteres. Não compartilhe a senha ao diagnosticar a configuração.

Com Docker Desktop ligado:

```powershell
docker compose config --quiet
docker compose up -d database
docker compose ps
```

Espere o serviço `database` ficar `healthy`. A aplicação Java executada no host usa `localhost:5434`, banco `jornada_db` e usuário `jornada_app`. A porta está publicada somente em `127.0.0.1`.

O volume `jornada_data` mantém os dados. Alterar a senha no `.env` após a inicialização não altera a senha já armazenada pelo PostgreSQL. Para parar o ambiente preservando o volume, use `docker compose down`, sem `-v`.

As variáveis `JORNADA_DB_URL` e `JORNADA_DB_USERNAME` permitem sobrescrever os padrões da aplicação. Evite valores antigos dessas variáveis no terminal ao testar outra configuração.

## Executar a aplicação

Com o banco de desenvolvimento pronto, execute na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação inicia na porta `8082`. Como ainda não há controllers de negócio, acessar `/` pode retornar `404`; isso não indica falha de inicialização. Para encerrar, pressione `Ctrl+C` no terminal.

O Flyway cria a estrutura por migrations; `spring.jpa.hibernate.ddl-auto=validate` deixa o Hibernate validar o mapeamento, sem gerar tabelas. Não edite migrations já aplicadas para evoluir o schema: crie uma nova versão.

## Organização atual

```text
src/
├── main/
│   ├── java/br/com/luismarangoni/jornada_api/
│   │   ├── JornadaApiApplication.java
│   │   └── funcionario/
│   │       ├── Funcionario.java
│   │       └── infra/persistencia/
│   │           ├── FuncionarioJpaEntity.java
│   │           └── FuncionarioJpaRepository.java
│   └── resources/
│       ├── application.properties
│       └── db/migration/V1__criar_tabela_funcionarios.sql
└── test/
    ├── java/br/com/luismarangoni/jornada_api/
    │   ├── JornadaApiApplicationTests.java
    │   ├── PostgresTestConfiguration.java
    │   └── funcionario/
    │       ├── FuncionarioTest.java
    │       └── infra/persistencia/FuncionarioJpaRepositoryTest.java
    └── resources/application.properties
```

## Roadmap

1. **Base do domínio:** estrutura inicial e testes de funcionário — concluída.
2. **Cadastro persistente — em andamento:** PostgreSQL, migration e repositório JPA implementados; faltam os casos de uso, endpoints e jornada prevista.
3. **Controle de acesso:** contas e permissões de funcionário/RH, separadas dos dados profissionais.
4. **Marcações e apuração:** entradas, saídas, intervalos e identificação de pendências.
5. **Ajustes auditáveis:** solicitações, aprovação pelo RH e preservação do histórico original.
6. **Notificações:** e-mails de pendências, inicialmente capturados em ambiente local.
7. **Integração Python:** automações e relatórios consumindo a API, sem escrita direta no banco nem duplicação das regras de jornada.
8. **Interface:** marcação, consulta pessoal e painel do RH.

Cada etapa deve ser acompanhada de testes e documentação. As funcionalidades planejadas não estão disponíveis na versão atual.

## Escopo

Este é um projeto educacional com dados fictícios, não um registrador oficial de ponto nem uma aplicação pronta para produção. Eventuais cálculos financeiros serão simulações sujeitas à conferência, sem desconto automático em folha. Adequação para uso trabalhista real está fora do escopo desta etapa.

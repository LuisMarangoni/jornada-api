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
- Caso de uso `CadastrarFuncionario`, com consulta prévia de matrícula e persistência por uma interface independente do JPA.
- Adaptador JPA com tradução específica da violação de matrícula única.
- Endpoint `POST /funcionarios` com DTOs em records e validação da entrada.
- Endpoint `GET /funcionarios/{id}` com DTO de resposta e tratamento de inexistência.
- Erros HTTP de validação e matrícula duplicada padronizados com `ProblemDetail`.
- 80 testes: 12 do domínio de funcionário, 7 do domínio de marcação/apuração, 24 do controller HTTP, 5 de segurança, 9 do adaptador, 3 do cadastro, 2 da consulta, 2 da listagem, 2 da atualização, 2 da alteração de status, 5 do registro de marcação, 2 da consulta de marcações, 2 da apuração, 2 do repositório JPA e 1 de contexto Spring.
- GitHub Actions executa a suíte com Java 21 em pushes e pull requests para `main`.

O cadastro, a consulta por ID, a atualização, a alteração de status, a listagem paginada, o registro/consulta de marcações e o resumo da jornada estão disponíveis por HTTP. As rotas são protegidas por JWT compatível com o `users-api`; a emissão de tokens permanece centralizada nesse serviço. O DTO de entrada valida formato de e-mail e limites de tamanho; o domínio mantém suas próprias verificações de campos obrigatórios e normalização. A unicidade da matrícula é garantida no PostgreSQL e traduzida para conflito HTTP. IDs inexistentes e parâmetros de paginação inválidos retornam `ProblemDetail`.

## Separação de responsabilidades

- `Funcionario`: domínio Java, com validação de campos obrigatórios e normalização.
- `CadastrarFuncionario`: coordena o cadastro e depende apenas do contrato `FuncionarioRepository`.
- `FuncionarioRepository`: porta de saída que define a consulta por matrícula e a gravação, retornando o ID gerado.
- `FuncionarioRepositoryAdapter`: implementa essa porta, converte o domínio para `FuncionarioJpaEntity` e delega ao Spring Data JPA.
- `FuncionarioConfiguration`: registra o caso de uso como bean, sem adicionar anotações Spring à aplicação ou ao domínio.
- `FuncionarioController`: recebe o DTO validado, executa o caso de uso e devolve um DTO com o ID, sem expor a entidade JPA.
- `BuscarFuncionario`: consulta um funcionário pela porta e traduz ausência em `FuncionarioNaoEncontradoException`.
- `AlterarStatusFuncionario`: verifica a existência e altera somente o estado ativo/inativo do funcionário.
- `AtualizarFuncionario`: verifica a existência, valida os novos dados pelo domínio e preserva matrícula e estado durante a atualização.
- `FuncionarioResponse`: DTO de saída que preserva ID, dados normalizados e estado ativo, sem expor a entidade JPA.
- `TratadorGlobalDeErros`: trata erros HTTP globalmente; validação retorna `400` e matrícula duplicada retorna `409`, sem expor a causa SQL.

O cadastro consulta a matrícula já normalizada antes de salvar. Essa consulta não elimina a possibilidade de concorrência: a restrição `uk_funcionarios_matricula` é a garantia final. O adaptador converte especificamente essa violação em `MatriculaJaCadastradaException`, preservando a causa original e propagando outros erros de integridade. Ainda não há teste de cadastros simultâneos.

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

Os testes do caso de uso também dispensam Docker e usam Mockito para simular a porta de persistência:

```powershell
.\mvnw.cmd "-Dtest=CadastrarFuncionarioTest" test
```

Eles verificam o envio de dados normalizados, o bloqueio de matrícula existente e a rejeição de dados inválidos antes de acessar o repositório.

Os testes de contexto e persistência utilizam Testcontainers com PostgreSQL 17 em um container temporário, com porta dinâmica e credenciais fictícias. `@ServiceConnection` fornece a conexão ao Spring; não é necessário `.env`, senha real, API em execução ou banco do Compose. O arquivo `src/test/resources/application.properties` mantém a configuração dos testes separada da configuração local.

O Flyway executa as migrations nesse banco temporário e o Hibernate valida o mapeamento. Os testes de repositório verificam gravação e leitura após limpar o contexto de persistência, além da rejeição de matrícula duplicada. As transações dos testes são revertidas ao final. Uma mensagem SQL de chave duplicada é esperada no cenário que provoca essa violação; a suíte deve terminar com sucesso.

O Spring gerencia o ciclo de vida do container de teste. Os dados do volume de desenvolvimento não são utilizados. A execução completa no GitHub Actions também depende do Docker disponível no runner.

Os testes do adaptador verificam a persistência do domínio, a consulta de existência por matrícula, a tradução de duplicação e a preservação de outros erros de integridade. O cenário de nome acima do limite da coluna provoca um erro SQL intencionalmente.

Os testes HTTP usam MockMvc com a aplicação e a persistência reais no banco temporário. Verificam `201` com gravação, `400` para e-mail inválido, paginação inválida ou tamanho fora do limite, `409` para matrícula duplicada, `200` na consulta e listagem paginada e `404` para ID inexistente, incluindo o formato `application/problem+json` dos erros.

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

A aplicação inicia na porta `8082`. Não há rota para `/`, portanto acessá-la pode retornar `404`; isso não indica falha de inicialização. Para encerrar, pressione `Ctrl+C` no terminal.

O Flyway cria a estrutura por migrations; `spring.jpa.hibernate.ddl-auto=validate` deixa o Hibernate validar o mapeamento, sem gerar tabelas. Não edite migrations já aplicadas para evoluir o schema: crie uma nova versão.

## Cadastro de funcionário

Com a API e o banco de desenvolvimento rodando, envie:

```http
POST http://localhost:8082/funcionarios
Content-Type: application/json
```

```json
{
  "matricula": "MAT-001",
  "nome": "Ana Silva",
  "email": "ana@email.com"
}
```

Resposta esperada: `201 Created`, com `{"id": 1}` (ID ilustrativo; o banco gera o valor real).

Todos os campos são obrigatórios. Os limites da entrada são 50 caracteres para matrícula, 150 para nome e 254 para e-mail, que também deve ter formato válido. A matrícula é normalizada removendo espaços nas extremidades, mas não é convertida para maiúsculas; a verificação atual distingue maiúsculas e minúsculas.

Dados inválidos retornam `400 Bad Request` com `ProblemDetail` e a propriedade `erros`, que agrupa mensagens por campo. Matrícula já cadastrada retorna `409 Conflict`. O tratamento segue o formato Problem Details suportado pelo Spring (RFC 9457).

Para consultar um funcionário:

```http
GET http://localhost:8082/funcionarios/1
```

Um funcionário existente retorna `200 OK` com:

```json
{
  "id": 1,
  "matricula": "MAT-001",
  "nome": "Ana Silva",
  "email": "ana@email.com",
  "ativo": true
}
```

Um ID inexistente retorna `404 Not Found` com `title` `Funcionário não encontrado` e o detalhe correspondente. O endpoint ainda não exige autenticação e deve permanecer restrito ao ambiente local.

## Atualização de funcionário

Para alterar nome e e-mail sem modificar a matrícula ou o estado do funcionário:

```http
PUT http://localhost:8082/funcionarios/1
Content-Type: application/json
```

```json
{
  "nome": "Bruno Souza",
  "email": "bruno@email.com"
}
```

Um funcionário existente retorna `200 OK` com os dados atualizados. E-mail inválido ou campos ausentes retornam `400`; ID inexistente retorna `404`. A matrícula e o estado `ativo` não são alterados por este endpoint.

## Ativação e desativação

Para alterar somente o estado do funcionário:

```http
PATCH http://localhost:8082/funcionarios/1/ativo
Content-Type: application/json
```

```json
{
  "ativo": false
}
```

O endpoint retorna `200 OK` com o funcionário atualizado. O campo `ativo` é obrigatório; corpo sem esse campo retorna `400`, e ID inexistente retorna `404`. Desativar preserva matrícula, nome e e-mail, e será usado futuramente para bloquear marcações de ponto de funcionários inativos.

## Registro de marcação

Funcionários ativos podem registrar uma marcação:

```http
POST http://localhost:8082/funcionarios/1/marcacoes
Content-Type: application/json
```

```json
{
  "tipo": "ENTRADA"
}
```

Os tipos disponíveis são `ENTRADA`, `INICIO_INTERVALO`, `FIM_INTERVALO` e `SAIDA`. O horário é gerado pela API em UTC. O registro retorna `201 Created`; funcionário inexistente retorna `404` e funcionário inativo retorna `422`.

Para consultar o histórico:

```http
GET http://localhost:8082/funcionarios/1/marcacoes
```

A resposta retorna uma lista ordenada pelo horário da ocorrência. A sequência permitida é `ENTRADA` → `INICIO_INTERVALO` ou `SAIDA`, `INICIO_INTERVALO` → `FIM_INTERVALO`, `FIM_INTERVALO` → `INICIO_INTERVALO` ou `SAIDA`, e `SAIDA` → `ENTRADA`. A primeira marcação deve ser `ENTRADA`; sequências inválidas retornam `422`.

## Apuração da jornada

Para consultar o resumo de uma jornada encerrada:

```http
GET http://localhost:8082/funcionarios/1/jornada/resumo
```

A resposta informa os minutos trabalhados e os minutos de intervalo:

```json
{
  "funcionarioId": 1,
  "minutosTrabalhados": 480,
  "minutosIntervalo": 60
}
```

O cálculo considera as marcações `ENTRADA`, `INICIO_INTERVALO`, `FIM_INTERVALO` e `SAIDA`. Jornadas ainda abertas não são apuradas nesta etapa.

Teste manual no PowerShell, em um segundo terminal:

```powershell
$corpo = @{
    matricula = "MAT-001"
    nome = "Ana Silva"
    email = "ana@email.com"
} | ConvertTo-Json

$resposta = Invoke-WebRequest -UseBasicParsing -Method Post -Uri "http://localhost:8082/funcionarios" -ContentType "application/json; charset=utf-8" -Body ([Text.Encoding]::UTF8.GetBytes($corpo))
$resposta.StatusCode
$resposta.Content
```

Esse comando cria um registro persistente no banco de desenvolvimento. Repetir a mesma matrícula deve retornar `409`; não é necessário apagar o banco para testar outra matrícula. O endpoint ainda não exige autenticação: use apenas dados fictícios em ambiente local, sem exposição pública.

## Organização atual

```text
src/
├── main/
│   ├── java/br/com/luismarangoni/jornada_api/
│   │   ├── JornadaApiApplication.java
│   │   ├── infra/TratadorGlobalDeErros.java
│   │   └── funcionario/
│   │       ├── Funcionario.java
│   │       ├── api/
│   │       │   ├── FuncionarioController.java
│   │       │   └── dto/
│   │       │       ├── CadastrarFuncionarioRequest.java
│   │       │       └── FuncionarioCriadoResponse.java
│   │       ├── aplicacao/
│   │       │   ├── CadastrarFuncionario.java
│   │       │   ├── MatriculaJaCadastradaException.java
│   │       │   └── porta/FuncionarioRepository.java
│   │       └── infra/
│   │           ├── configuracao/FuncionarioConfiguration.java
│   │           └── persistencia/
│   │               ├── FuncionarioJpaEntity.java
│   │               ├── FuncionarioJpaRepository.java
│   │               └── FuncionarioRepositoryAdapter.java
│   └── resources/
│       ├── application.properties
│       └── db/migration/V1__criar_tabela_funcionarios.sql
└── test/
    ├── java/br/com/luismarangoni/jornada_api/
    │   ├── JornadaApiApplicationTests.java
    │   ├── PostgresTestConfiguration.java
    │   └── funcionario/
    │       ├── FuncionarioTest.java
    │       ├── api/FuncionarioControllerTest.java
    │       ├── aplicacao/CadastrarFuncionarioTest.java
    │       └── infra/persistencia/
    │           ├── FuncionarioJpaRepositoryTest.java
    │           └── FuncionarioRepositoryAdapterTest.java
    └── resources/application.properties
```

## Listagem de funcionÃ¡rios

O endpoint `GET /funcionarios` lista os funcionÃ¡rios com paginaÃ§Ã£o:

```http
GET http://localhost:8082/funcionarios?pagina=0&tamanho=10
```

`pagina` comeÃ§a em zero e `tamanho` deve estar entre 1 e 100. A resposta inclui `conteudo` e os metadados `pagina`, `tamanho`, `totalElementos` e `totalPaginas`. Uma pÃ¡gina sem registros retorna `200` com `conteudo` vazio. ParÃ¢metros invÃ¡lidos retornam `400` com `ProblemDetail`.

## Autenticação

O `jornada-api` atua como Resource Server e valida tokens JWT emitidos pelo `users-api`. Os dois serviços devem utilizar o mesmo valor de `JWT_SECRET`, configurado apenas no `.env` local. A API não possui endpoint de login próprio.

Operações administrativas de cadastro, atualização e alteração de status exigem os perfis `SUPORTE` ou `ADMIN`. Consultas e marcações exigem apenas autenticação nesta etapa; a validação de acesso do usuário ao próprio funcionário será adicionada quando o vínculo entre as identidades for modelado.

A documentação OpenAPI permanece pública; as demais rotas exigem o header:

```http
Authorization: Bearer <token-jwt>
```

Testes HTTP de negócio desabilitam os filtros para isolarem as regras da aplicação. A classe `SegurancaControllerTest` mantém os filtros ativos e verifica `401` sem token e passagem da autenticação com JWT simulado.

## Roadmap

1. **Base do domínio:** estrutura inicial e testes de funcionário — concluída.
2. **Cadastro persistente — concluída:** cadastro, consulta, listagem paginada, validação, tratamento de conflitos e persistência implementados.
3. **Controle de acesso — em andamento:** validação de JWT compartilhada com o `users-api` implementada; regras de autorização por perfil serão adicionadas nas próximas entregas.
4. **Marcações e apuração — em andamento:** registro, consulta, validação de sequência e resumo de minutos trabalhados/intervalos implementados; pendências e fechamento de jornada serão desenvolvidos nas próximas entregas.
5. **Ajustes auditáveis:** solicitações, aprovação pelo RH e preservação do histórico original.
6. **Notificações:** e-mails de pendências, inicialmente capturados em ambiente local.
7. **Integração Python:** automações e relatórios consumindo a API, sem escrita direta no banco nem duplicação das regras de jornada.
8. **Interface:** marcação, consulta pessoal e painel do RH.

Cada etapa deve ser acompanhada de testes e documentação. As funcionalidades planejadas não estão disponíveis na versão atual.

## Escopo

Este é um projeto educacional com dados fictícios, não um registrador oficial de ponto nem uma aplicação pronta para produção. Eventuais cálculos financeiros serão simulações sujeitas à conferência, sem desconto automático em folha. Adequação para uso trabalhista real está fora do escopo desta etapa.

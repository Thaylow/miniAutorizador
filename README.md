# Miniautorizador

> Microserviço de autorização de requisições, desenvolvido com Spring Boot e Java 17.

## Descrição

Miniautorizador é uma aplicação backend que expõe endpoints REST para criar cartões e realizar autorizações de transações. Ela persiste dados em um banco relacional MySQL.

## Funcionalidades principais

- Criação de cartão, consulta de saldo e realizações de transações via API REST.
- Persistência de dados em MySQL.
- Configuração flexível por meio de variáveis de ambiente.
- Containerização com Docker e orquestração via Docker Compose.

## Estrutura do projeto

```text
miniautorizador/           # Diretório raiz do projeto
│
├── src/
│   ├── main/
│   │   ├── java/           # Código-fonte Java
│   │   └── resources/      # Configurações e templates
│   │       └── application.properties
│   └── test/               # Testes automatizados
│
├── Dockerfile              # Definição da imagem Docker (build da aplicação)
├── docker-compose.yml      # Orquestração de contêineres (MySQL e app)
├── pom.xml                 # Gerenciador de dependências Maven
└── .dockerignore           # Arquivos/pastas excluídas do contexto Docker
```

## Configuração

As propriedades de conexão com o banco são definidas por variáveis de ambiente e referenciadas no `application.properties`:

```properties
# === DataSource ===
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=${SPRING_DATASOURCE_DRIVER}

# === JPA / Hibernate ===
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO}
spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL}
spring.jpa.properties.hibernate.format_sql=${SPRING_JPA_FORMAT_SQL}
spring.jpa.database-platform=${SPRING_JPA_DATABASE_PLATFORM}
```

### Exemplo de variáveis no `docker-compose.yml`

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/miniautorizador
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: ""
  SPRING_DATASOURCE_DRIVER: com.mysql.cj.jdbc.Driver
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
  SPRING_JPA_SHOW_SQL: true
  SPRING_JPA_FORMAT_SQL: true
  SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.MySQLDialect
```

## Como executar com Docker Compose

1. **Clone o repositório** e acesse a pasta do projeto:

   ```bash
   git clone <url-do-repo>
   cd miniautorizador
   ```

2. **Build e startup** dos contêineres:

   ```bash
   mvn clean package -DskipTests
   ```

   ```bash
   docker-compose up --build
   ```

   - O serviço `mysql` será iniciado (criando o schema `miniautorizador`).
   - O serviço `autorizador` (sua aplicação Spring Boot) será construído e executado.

3. **Acesse a API** em:

   ```
   http://localhost:8080
   ```

## Endpoints

### POST /cartoes

- **Descrição:** Cria um novo cartão com saldo inicial.
- **Request:** `POST /cartoes`
- **Body:** JSON contendo os dados do titular (número do cartão e senha).
- **Response:** `201 Created` com o objeto do cartão criado, incluindo `senha`, `numeroCartao`.

### GET /cartoes/{id}

- **Descrição:** Recupera informações e saldo do cartão especificado.
- **Request:** `GET /cartoes/{id}`
- **Response:** `200 OK` com o saldo do cartão: `saldo`.

### POST /transacoes

- **Descrição:** Realiza uma transação em um cartão.
- **Request:** `POST /transacoes`
- **Body:** JSON contendo `numeroCartao`, `senha` e `valor`.
- **Response:** `201 Created` com mensagem `OK`.

````

4. **Logs e saúde**:

   ```bash
   docker-compose logs -f miniautorizador
   docker-compose logs -f mysql
````

*Desenvolvido por Thaylow Viana*


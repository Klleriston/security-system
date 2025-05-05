# FIAP Security System API

## Visão Geral
A API do FIAP Security System fornece um conjunto de endpoints para gerenciar funcionários, lidar com autenticação de usuários e relatar incidentes de segurança. A API é destinada ao uso em sistemas de segurança onde incidentes precisam ser registrados, usuários gerenciados e acesso baseado em funções é aplicado.

## Funcionalidades
* **Gerenciamento de Funcionários**: Criar, atualizar e recuperar registros de funcionários.
* **Autenticação de Usuários**: Registrar novos usuários e gerenciar sessões de login.
* **Gerenciamento de Incidentes**: Criar, atualizar, excluir e recuperar incidentes de segurança.

## Endpoints da API

### 1. Gerenciamento de Funcionários
* **Criar Usuário**: `POST /api/employees`
  * Adiciona um novo funcionário ao sistema.
* **Obter Todos os Usuários**: `GET /api/employees`
  * Recupera todos os registros de funcionários.
* **Obter Funcionário por ID**: `GET /api/employees/{id}`
  * Recupera os detalhes de um funcionário específico.
* **Obter Funcionários por Função**: `GET /api/employees/role/{role}`
  * Recupera funcionários com base em sua função.

### 2. Autenticação
* **Registrar**: `POST /api/auth/register`
  * Registra um novo usuário no sistema.
* **Login**: `POST /api/auth/login`
  * Autentica um usuário existente e fornece um token.

### 3. Gerenciamento de Incidentes
* **Obter Todos os Incidentes**: `GET /api/incidents`
  * Recupera todos os incidentes relatados.
* **Criar Incidente**: `POST /api/incidents`
  * Relata um novo incidente.
* **Obter Incidente por ID**: `GET /api/incidents/{id}`
  * Recupera um incidente específico pelo seu ID.
* **Obter Incidentes por Status**: `GET /api/incidents/status/{status}`
  * Recupera incidentes filtrados por seu status.
* **Atualizar Status do Incidente**: `PATCH /api/incidents/{id}/status`
  * Atualiza o status de um incidente específico.
* **Excluir Incidente**: `DELETE /api/incidents/{id}`
  * Exclui um incidente específico do sistema.

## Começando

### Pré-requisitos
* **Java 11+**: O backend é construído usando Java, requerendo versão 11 ou posterior.
* **Docker**: Usado para containerização e implantação fácil.

### Executando a API
1. Clone o repositório.
2. Certifique-se de que o Docker esteja instalado e em execução.
3. Use o Docker Compose para construir e iniciar a aplicação:
`docker-compose up --build`

4. A API estará disponível em `http://localhost:8080`.

## Uso
Você pode usar o Postman para interagir com os endpoints. Uma coleção (`FIAP.postman_collection.json`) é fornecida para testes fáceis. Importe-a no Postman e execute as requisições diretamente.

## Testes

### Executando Testes
O projeto inclui testes unitários e testes de integração. Você pode executar todos os testes usando Maven:
`mvn test`


### Tipos de Testes
- **Testes Unitários**: Testam componentes individuais isoladamente
- **Testes de API**: Testam os endpoints da API com REST Assured
- **Testes BDD**: Testes baseados em desenvolvimento orientado a comportamento com Cucumber

### Documentação de Testes
O conjunto de testes inclui:
- Arquivos de feature do Cucumber que descrevem cenários de comportamento
- Testes de API que validam os endpoints REST
- Validação de esquema JSON para respostas da API

## Configuração do Banco de Dados

A aplicação está configurada para usar Oracle como seu banco de dados. A configuração padrão é:
```
spring.datasource.url=jdbc:oracle:thin:@localhost:9445/XEPDB1
spring.datasource.username=system
spring.datasource.password=password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=update
spring.flyway.url=jdbc:oracle:thin:@localhost:9445/XEPDB1
spring.flyway.user=system
spring.flyway.password=password
spring.flyway.enabled=false
```

## Modelos de Dados

### Incidentes
Incidentes podem ter os seguintes status:
- `SOLVED` (Resolvido)
- `PENDING` (Pendente)
- `UNSOLVED` (Não Resolvido)

E os seguintes tipos:
- `FRAUD` (Fraude)
- `ROBBERY` (Roubo)
- `THEFT` (Furto)
- `KIDNAPPING` (Sequestro)

### Funcionários
Funcionários podem ter as seguintes funções:
- `POLICE_OFFICER` (Policial)
- `ADMIN` (Administrador)

## Tecnologias Utilizadas
* **Spring Boot**: Para desenvolvimento da API REST.
* **PostgreSQL**: Como banco de dados primário para armazenar dados de usuários e incidentes.
* **Oracle**: Configuração alternativa de banco de dados fornecida.
* **Docker**: Para containerização.
* **Postman**: Para testar e documentar endpoints da API.
* **JUnit 5**: Para testes unitários e de integração.
* **Cucumber**: Para testes de desenvolvimento orientado a comportamento.
* **REST Assured**: Para testes de API.
* **Flyway**: Para migrações de banco de dados.

## Estrutura do Repositório
O repositório principal está disponível em: [https://github.com/Klleriston/security-system/tree/master](https://github.com/Klleriston/security-system/tree/master)
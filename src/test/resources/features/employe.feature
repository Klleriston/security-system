Feature: Gerenciamento de Funcionários
  Como um administrador
  Eu quero poder gerenciar os funcionários do sistema
  Para controlar quem tem acesso às funcionalidades

  Scenario: Criação de novo funcionário
    Given que eu estou autenticado como um usuário com role "ADMIN"
    When eu faço uma requisição POST para "/api/employees" com os dados:
      | documentId  | 12345678900               |
      | role        | POLICE_OFFICER            |
    Then o status da resposta deve ser 200
    And um novo funcionário deve ser criado no sistema
    And a resposta deve conter os dados do funcionário criado

  Scenario: Busca de funcionários por role
    Given que eu estou autenticado como um usuário com role "ADMIN"
    And existem funcionários com role "POLICE_OFFICER" no sistema
    When eu faço uma requisição GET para "/api/employees/role/POLICE_OFFICER"
    Then o status da resposta deve ser 200
    And a resposta deve conter uma lista dos funcionários com role "POLICE_OFFICER"

  Scenario: Tentativa de criação de funcionário sem informar documentId
    Given que eu estou autenticado como um usuário com role "ADMIN"
    When eu faço uma requisição POST para "/api/employees" sem informar o documentId
    Then o status da resposta deve ser 400
    And a resposta deve conter uma mensagem de erro "Informe a matricula do funcionario"
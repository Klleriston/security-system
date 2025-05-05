Feature: Gerenciamento de Incidentes
  Como um policial
  Eu quero poder registrar e gerenciar incidentes
  Para manter um registro de ocorrências e seu estado atual

  Scenario: Registro de novo incidente
    Given que eu estou autenticado como um usuário com role "POLICE_OFFICER"
    And existe um funcionário com ID 1 no sistema
    When eu faço uma requisição POST para "/api/incidents" com os dados:
      | title       | Roubo na Avenida Paulista      |
      | type        | ROBBERY                        |
      | description | Roubo de celular por assaltante em moto |
      | localization| Avenida Paulista, 1000         |
      | status      | PENDING                        |
      | responsibleId | 1                            |
    Then o status da resposta deve ser 200
    And um novo incidente deve ser criado no sistema
    And a resposta deve conter os dados do incidente criado

  Scenario: Atualização de status de incidente
    Given que eu estou autenticado como um usuário com role "POLICE_OFFICER"
    And existe um incidente com ID 1 e status "PENDING" no sistema
    When eu faço uma requisição PATCH para "/api/incidents/1/status" com o novo status "SOLVED"
    Then o status da resposta deve ser 200
    And o incidente com ID 1 deve ter seu status atualizado para "SOLVED"

  Scenario: Tentativa de exclusão de incidente sem permissão
    Given que eu estou autenticado como um usuário com role "POLICE_OFFICER"
    And existe um incidente com ID 1 no sistema
    When eu faço uma requisição DELETE para "/api/incidents/1"
    Then o status da resposta deve ser 403
    And o incidente não deve ser removido do sistema
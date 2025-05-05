Feature: Autenticação de Usuários
  Como um usuário do sistema de segurança
  Eu quero poder me autenticar no sistema
  Para que eu possa acessar as funcionalidades de acordo com minha função

  Scenario: Login com credenciais válidas
    Given que eu tenho um usuário cadastrado no sistema com username "policial" e senha "senha123"
    When eu faço uma requisição POST para "/api/auth/login" com as credenciais corretas
    Then o status da resposta deve ser 200
    And a resposta deve conter um token JWT válido

  Scenario: Login com credenciais inválidas
    Given que eu tenho um usuário cadastrado no sistema
    When eu faço uma requisição POST para "/api/auth/login" com senha incorreta
    Then o status da resposta deve ser 401
    And a resposta deve conter uma mensagem de erro "username ou senha inválidos"

  Scenario: Registro de novo usuário
    Given que eu não tenho um usuário cadastrado com username "novoPolicial"
    When eu faço uma requisição POST para "/api/auth/register" com username "novoPolicial", senha "senha123" e role "POLICE_OFFICER"
    Then o status da resposta deve ser 200
    And um novo usuário deve ser criado no sistema
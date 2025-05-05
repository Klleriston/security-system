package com.fiap.security_system.bdd.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthSteps {

    private Response response;
    private String username;
    private String password;
    private String role;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @After
    public void tearDown() {
    }

    @Given("que eu tenho um usuário cadastrado no sistema com username {string} e senha {string}")
    public void queEuTenhoUmUserCadastradoNoSistemaComUsernameESenha(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "POLICE_OFFICER";

        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                username, password, role
        );

        try {
            int statusCode = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/api/auth/register")
                    .getStatusCode();

            if (statusCode != 200 && statusCode != 400) {
                System.out.println("Registro de usuário retornou status code inesperado: " + statusCode);
            }
        } catch (Exception e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    @Given("que eu não tenho um usuário cadastrado com username {string}")
    public void queEuNaoTenhoUmUserCadastradoComUsername(String username) {
        this.username = username;
        this.password = "senha123";
        this.role = "POLICE_OFFICER";
    }

    @When("eu faço uma requisição POST para {string} com as credenciais corretas")
    public void euFacoUmaReqPOSTParaComAsCredenciaisCorretas(String endpoint) {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @When("eu faço uma requisição POST para {string} com senha incorreta")
    public void euFacoUmaReqPOSTParaComSenhaIncorreta(String endpoint) {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, "senha_incorreta"
        );

        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @When("eu faço uma requisição POST para {string} com username {string}, senha {string} e role {string}")
    public void euFaçoUmaReqPOSTParaComUsernameESenhaERole(String endpoint, String username, String password, String role) {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                username, password, role
        );

        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Then("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("a resposta deve conter um token JWT válido")
    public void aRespostaDeveConterUmTokenJWTVálido() {
        response.then().body("token", notNullValue());
    }

    @Then("a resposta deve conter uma mensagem de erro {string}")
    public void aRespostaDeveConterUmaMensagemDeErro(String errorMessage) {
        response.then().body("error", equalTo(errorMessage));
    }

    @Then("um novo usuário deve ser criado no sistema")
    public void umNovoUsuárioDeveSerCriadoNoSistema() {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }
}
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
import static org.hamcrest.Matchers.*;

public class EmployeeSteps {

    private Response response;
    private String adminToken;
    private String policeToken;
    private final String BASE_URL = "http://localhost:8080";

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        setupAuthentication();
    }

    @After
    public void tearDown() {
    }

    private void setupAuthentication() {
        createUserIfNotExists("admin", "admin123", "ADMIN");
        createUserIfNotExists("police", "police123", "POLICE_OFFICER");

        adminToken = login("admin", "admin123");
        policeToken = login("police", "police123");
    }

    private void createUserIfNotExists(String username, String password, String role) {
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

    private String login(String username, String password) {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Given("que eu estou autenticado como um usuário com role {string}")
    public void queEuEstouAutenticadoComoUmUsuárioComRole(String role) {
    }

    @Given("existem funcionários com role {string} no sistema")
    public void existemFuncionáriosComRoleNoSistema(String role) {
        String documentId = "12345678901";
        String requestBody = String.format(
                "{\"documentId\":\"%s\",\"role\":\"%s\"}",
                documentId, role
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(requestBody)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(200);
    }

    @When("eu faço uma requisição POST para {string} com os dados:")
    public void euFaçoUmaRequisiçãoPOSTParaComOsDados(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        String documentId = dataTable.cell(0, 1);
        String role = dataTable.cell(1, 1);

        String requestBody = String.format(
                "{\"documentId\":\"%s\",\"role\":\"%s\"}",
                documentId, role
        );

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @When("eu faço uma requisição GET para {string}")
    public void euFaçoUmaRequisiçãoGETParaStringRoleString(String endpoint) {
        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(endpoint);
    }

    @When("eu faço uma requisição POST para {string} sem informar o documentId")
    public void euFaçoUmaRequisiçãoPOSTParaSemInformarODocumentId(String endpoint) {
        String requestBody = "{\"role\":\"POLICE_OFFICER\"}";

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Then("um novo funcionário deve ser criado no sistema")
    public void umNovoFuncionárioDeveSerCriadoNoSistema() {
        Long employeeId = response.then()
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees/" + employeeId)
                .then()
                .statusCode(200)
                .body("id", equalTo(employeeId.intValue()));
    }

    @Then("a resposta deve conter os dados do funcionário criado")
    public void aRespostaDeveConterOsDadosDoFuncionárioCriado() {
        response.then()
                .body("id", notNullValue())
                .body("documentId", notNullValue())
                .body("role", notNullValue());
    }

    @Then("a resposta deve conter uma lista dos funcionários com role {string}")
    public void aRespostaDeveConterUmaListaDosFuncionáriosComRole(String role) {
        response.then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].role", equalTo(role));
    }
}
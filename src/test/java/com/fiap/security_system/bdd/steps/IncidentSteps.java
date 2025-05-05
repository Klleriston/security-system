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

public class IncidentSteps {

    private Response response;
    private String adminToken;
    private String policeToken;
    private Long incidentId;
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

    @Given("existe um funcionário com ID {int} no sistema")
    public void existeUmFuncionarioComIDNoSistema(Integer employeeId) {
        int statusCode = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees/" + employeeId)
                .getStatusCode();

        if (statusCode == 404) {
            String documentId = "12345678901";
            String requestBody = String.format(
                    "{\"documentId\":\"%s\",\"role\":\"POLICE_OFFICER\"}",
                    documentId
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
    }
    @Given("existe um incidente com ID {int} e status {string} no sistema")
    public void existeUmIncidenteComIDEStatusNoSistema(Integer incidentId, String status) {
        existeUmFuncionarioComIDNoSistema(1);

        Response checkResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .when()
                .get("/api/incidents/" + incidentId);

        if (checkResponse.getStatusCode() == 404) {
            String title = "Incidente de Teste";
            String description = "Descrição do incidente de teste";
            String localization = "Local do incidente de teste";
            String requestBody = String.format(
                    "{\"title\":\"%s\",\"type\":\"ROBBERY\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"%s\",\"responsibleId\":%d}",
                    title, description, localization, status, 1
            );

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + policeToken)
                    .body(requestBody)
                    .when()
                    .post("/api/incidents")
                    .then()
                    .statusCode(200);
        } else {
            String requestBody = String.format("{\"newStatus\":\"%s\"}", status);

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + policeToken)
                    .body(requestBody)
                    .when()
                    .patch("/api/incidents/" + incidentId + "/status")
                    .then()
                    .statusCode(200);
        }

        this.incidentId = incidentId.longValue();
    }

    @Given("existe um incidente com ID {int} no sistema")
    public void existeUmIncidenteComIDNoSistema(Integer incidentId) {
        existeUmIncidenteComIDEStatusNoSistema(incidentId, "PENDING");
    }

    @When("eu faço uma requisição POST para {string} com os dados:")
    public void euFaçoUmaReqPOSTParaComOsDados(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        String title = dataTable.cell(0, 1);
        String type = dataTable.cell(1, 1);
        String description = dataTable.cell(2, 1);
        String localization = dataTable.cell(3, 1);
        String status = dataTable.cell(4, 1);
        Long responsibleId = Long.parseLong(dataTable.cell(5, 1));

        String requestBody = String.format(
                "{\"title\":\"%s\",\"type\":\"%s\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"%s\",\"responsibleId\":%d}",
                title, type, description, localization, status, responsibleId
        );

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @When("eu faço uma requisição PATCH para {string} com o novo status {string}")
    public void euFaçoUmaReqPATCHParaComONovoStatus(String endpoint, String newStatus) {
        String requestBody = String.format("{\"newStatus\":\"%s\"}", newStatus);

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .body(requestBody)
                .when()
                .patch(endpoint);
    }

    @When("eu faço uma requisição DELETE para {string}")
    public void euFaçoUmaReqDELETEPara(String endpoint) {
        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .when()
                .delete(endpoint);
    }

    @Then("um novo incidente deve ser criado no sistema")
    public void umNovoIncidenteDeveSerCriadoNoSistema() {
        incidentId = response.then()
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .when()
                .get("/api/incidents/" + incidentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(incidentId.intValue()));
    }

    @Then("a resposta deve conter os dados do incidente criado")
    public void aRespostaDeveConterOsDadosDoIncidenteCriado() {
        response.then()
                .body("id", notNullValue())
                .body("title", notNullValue())
                .body("description", notNullValue())
                .body("localization", notNullValue())
                .body("status", notNullValue())
                .body("type", notNullValue())
                .body("responsible", notNullValue());
    }

    @Then("o incidente com ID {int} deve ter seu status atualizado para {string}")
    public void oIncidenteComIDDeveTerSeuStatusAtualizadoPara(Integer incidentId, String newStatus) {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .when()
                .get("/api/incidents/" + incidentId)
                .then()
                .statusCode(200)
                .body("status", equalTo(newStatus));
    }

    @Then("o incidente não deve ser removido do sistema")
    public void oIncidenteNaoDeveSerRemovidoDoSistema() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + policeToken)
                .when()
                .get("/api/incidents/" + incidentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(incidentId.intValue()));
    }
}
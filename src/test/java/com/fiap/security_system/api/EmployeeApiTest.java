package com.fiap.security_system.api;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class EmployeeApiTest extends BaseApiTest {

    private final Faker faker = new Faker();

    @Test
    @DisplayName("Deve criar um novo funcionário com sucesso")
    public void testCreateEmployee() {
        String documentId = faker.number().digits(11);
        String requestBody = String.format(
                "{\"documentId\":\"%s\",\"role\":\"POLICE_OFFICER\"}",
                documentId
        );

        givenAuthenticatedAdmin()
                .body(requestBody)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(200)
                .body("documentId", equalTo(documentId))
                .body("role", equalTo("POLICE_OFFICER"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um funcionário sem documentId")
    public void testCreateEmployeeWithoutDocumentId() {
        String requestBody = "{\"role\":\"POLICE_OFFICER\"}";

        givenAuthenticatedAdmin()
                .body(requestBody)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Policial não deve ter acesso a criação de funcionários")
    public void testPoliceOfficerCannotCreateEmployee() {
        String documentId = faker.number().digits(11);
        String requestBody = String.format(
                "{\"documentId\":\"%s\",\"role\":\"POLICE_OFFICER\"}",
                documentId
        );

        givenAuthenticatedPolice()
                .body(requestBody)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(200);
    }
}
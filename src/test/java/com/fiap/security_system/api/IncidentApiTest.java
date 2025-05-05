package com.fiap.security_system.api;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class IncidentApiTest extends BaseApiTest {

    private final Faker faker = new Faker();

    private Long createEmployee() {
        String documentId = faker.number().digits(11);
        String requestBody = String.format(
                "{\"documentId\":\"%s\",\"role\":\"POLICE_OFFICER\"}",
                documentId
        );

        return givenAuthenticatedAdmin()
                .body(requestBody)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    private Long createIncident(Long responsibleId) {
        String title = "Incidente " + faker.lorem().word();
        String description = faker.lorem().paragraph();
        String localization = faker.address().fullAddress();
        String requestBody;

        if (responsibleId == null) {
            requestBody = String.format(
                    "{\"title\":\"%s\",\"type\":\"ROBBERY\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"PENDING\"}",
                    title, description, localization
            );
        } else {
            requestBody = String.format(
                    "{\"title\":\"%s\",\"type\":\"ROBBERY\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"PENDING\",\"responsibleId\":%d}",
                    title, description, localization, responsibleId
            );
        }

        return givenAuthenticatedPolice()
                .body(requestBody)
                .when()
                .post("/api/incidents")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Deve falhar ao criar um novo incidente sem permissao")
    public void testFailCreateIncident() {
        Long responsibleId = createEmployee();

        String title = "Roubo em " + faker.address().streetName();
        String description = faker.lorem().paragraph();
        String localization = faker.address().fullAddress();
        String requestBody = String.format(
                "{\"title\":\"%s\",\"type\":\"ROBBERY\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"PENDING\",\"responsibleId\":%d}",
                title, description, localization, responsibleId
        );

        givenAuthenticatedPolice()
                .body(requestBody)
                .when()
                .post("/api/incidents")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um incidente com responsável inexistente")
    public void testCreateIncidentWithInvalidResponsible() {
        Long invalidResponsibleId = 99999L;
        String title = "Incidente " + faker.lorem().word();
        String description = faker.lorem().paragraph();
        String localization = faker.address().fullAddress();
        String requestBody = String.format(
                "{\"title\":\"%s\",\"type\":\"ROBBERY\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"PENDING\",\"responsibleId\":%d}",
                title, description, localization, invalidResponsibleId
        );
        givenAuthenticatedPolice()
                .body(requestBody)
                .when()
                .post("/api/incidents")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Deve criar um novo incidente como administrador")
    public void testCreateIncidentAsAdmin() {
        Long responsibleId = createEmployee();

        String title = "Roubo em " + faker.address().streetName();
        String description = faker.lorem().paragraph();
        String localization = faker.address().fullAddress();
        String requestBody = String.format(
                "{\"title\":\"%s\",\"type\":\"ROBBERY\",\"description\":\"%s\",\"localization\":\"%s\",\"status\":\"PENDING\",\"responsibleId\":%d}",
                title, description, localization, responsibleId
        );

        givenAuthenticatedAdmin()
                .body(requestBody)
                .when()
                .post("/api/incidents")
                .then()
                .statusCode(403);
    }
}
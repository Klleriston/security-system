package com.fiap.security_system.bdd;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TestContext {

    private static final String BASE_URL = "http://localhost:8080";
    private String adminToken;
    private String policeToken;
    private Response lastResponse;

    public TestContext() {
        RestAssured.baseURI = BASE_URL;
        setupAuthentication();
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

        int statusCode = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .getStatusCode();

        if (statusCode != 200 && statusCode != 400) {
            throw new AssertionError("Status code esperado: 200 ou 400, mas foi recebido: " + statusCode);
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

    public String getAdminToken() {
        return adminToken;
    }

    public String getPoliceToken() {
        return policeToken;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }
}
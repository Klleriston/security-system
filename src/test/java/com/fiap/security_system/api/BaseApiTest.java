package com.fiap.security_system.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {

    protected static final String BASE_URI = "http://localhost:8080";
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;
    protected String adminToken;
    protected String policeToken;

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.defaultParser = Parser.JSON;

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        setupAuthentication();
    }

    private void setupAuthentication() {
        try {
            registerUser("admin", "admin123", "ADMIN");
        } catch (Exception e) {
        }

        try {
            registerUser("police", "police123", "POLICE_OFFICER");
        } catch (Exception e) {
        }

        adminToken = login("admin", "admin123");
        policeToken = login("police", "police123");
    }

    private void registerUser(String username, String password, String role) {
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                username, password, role
        );

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/register");
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

    protected RequestSpecification givenAuthenticatedAdmin() {
        if (isTokenExpired(adminToken)) {
            adminToken = login("admin", "admin123");
        }

        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + adminToken);
    }

    protected RequestSpecification givenAuthenticatedPolice() {
        if (isTokenExpired(policeToken)) {
            policeToken = login("police", "police123");
        }

        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + policeToken);
    }

    private boolean isTokenExpired(String token) {
        if (token == null) return true;

        try {
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/api/employees")
                    .then()
                    .statusCode(200);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
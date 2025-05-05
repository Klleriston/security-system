package com.fiap.security_system.api;

import com.github.javafaker.Faker;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthApiTest extends BaseApiTest {

    private final Faker faker = new Faker();

    @Test
    @DisplayName("Deve realizar login com sucesso")
    public void testLoginSuccess() {
        String username = "police";
        String password = "police123";
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/auth/login-response-schema.json"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar login com credenciais inválidas")
    public void testLoginFailure() {
        String username = "police";
        String invalidPassword = "senha_incorreta";
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, invalidPassword
        );

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("username ou senha inválidos"));
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    public void testRegisterSuccess() {
        String newUsername = "teste_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "senha123";
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"POLICE_OFFICER\"}",
                newUsername, password
        );

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200);

        String loginBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                newUsername, password
        );

        given()
                .spec(requestSpec)
                .body(loginBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("Deve falhar ao tentar registrar um usuário que já existe")
    public void testRegisterDuplicateUser() {
        String username = "police"; // Usuário já existente
        String password = "police123";
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"POLICE_OFFICER\"}",
                username, password
        );

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(400)
                .body(equalTo("usuario ja existe"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar registrar um usuário sem informar a role")
    public void testRegisterWithoutRole() {
        String newUsername = "teste_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "senha123";
        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                newUsername, password
        );

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(401)
                .body(equalTo("informe a role do usuario"));
    }
}
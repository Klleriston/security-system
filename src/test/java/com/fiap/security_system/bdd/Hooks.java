package com.fiap.security_system.bdd;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

public class Hooks {

    private static final String BASE_URL = "http://localhost:8080";
    private static TestContext testContext;

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("Iniciando cenário: " + scenario.getName());

        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        if (testContext == null) {
            testContext = new TestContext();
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        System.out.println("Finalizando cenário: " + scenario.getName());
        System.out.println("Status: " + (scenario.isFailed() ? "FALHOU" : "PASSOU"));
    }

    public static TestContext getTestContext() {
        return testContext;
    }
}
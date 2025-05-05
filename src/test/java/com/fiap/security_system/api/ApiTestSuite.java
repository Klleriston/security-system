package com.fiap.security_system.api;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.fiap.security_system.api")
public class ApiTestSuite {

    @Test
    void emptySuiteTest() {
    }
}
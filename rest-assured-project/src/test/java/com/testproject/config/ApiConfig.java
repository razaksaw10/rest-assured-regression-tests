package com.testproject.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base configuration class for all API tests.
 * Sets up RestAssured defaults, base URI, and shared request specs.
 */
public class ApiConfig {

    protected static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    protected static final int MAX_RESPONSE_TIME_MS = 5000; // 3 seconds max

    protected static RequestSpecification requestSpec;

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}

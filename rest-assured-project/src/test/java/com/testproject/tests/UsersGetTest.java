package com.testproject.tests;

import com.testproject.config.ApiConfig;
import com.testproject.models.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for the /users endpoint.
 * Tests various GET operations to validate user data.
 */
@Epic("API Regression Tests")
@Feature("Users API - GET Operations")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersGetTest extends ApiConfig {

    private static final String USERS_ENDPOINT = "/users";

    // =========================================================
    // TC-010: Get all users
    // =========================================================
    @Test
    @Order(1)
    @Story("Get all users")
    @Description("Verify GET /users returns 200 and a list of 10 users")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("TC-010: GET /users - Returns 200 and 10 users")
    public void getAllUsers_ShouldReturn200AndCorrectCount() {
        Response response = given()
                .spec(requestSpec)
            .when()
                .get(USERS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                // 2. Response body içerisinde beklenen değer kontrolleri
                .body("$", hasSize(10))
                .body("[0].id",       notNullValue())
                .body("[0].name",     not(emptyOrNullString()))
                .body("[0].email",    not(emptyOrNullString()))
                .body("[0].username", not(emptyOrNullString()))
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        List<User> users = response.jsonPath().getList(".", User.class);
        assertEquals(10, users.size(), "10 kullanıcı dönmeli");

        System.out.println("✅ TC-010 PASSED: " + users.size() + " kullanıcı alındı. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-011: Get user by ID - validate fields
    // =========================================================
    @Test
    @Order(2)
    @Story("Get single user")
    @Description("Verify GET /users/1 returns correct user with name, email, and username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-011: GET /users/1 - Returns correct user data")
    public void getUserById_ShouldReturnCorrectUserDetails() {
        int userId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", userId)
            .when()
                .get(USERS_ENDPOINT + "/{id}")
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                // 2. Response body içerisinde beklenen değer kontrolleri
                .body("id",       equalTo(userId))
                .body("name",     equalTo("Leanne Graham"))
                .body("username", equalTo("Bret"))
                .body("email",    equalTo("Sincere@april.biz"))
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-011 PASSED: Kullanıcı doğrulandı. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-012: Email format validation for all users
    // =========================================================
    @Test
    @Order(3)
    @Story("Validate user email format")
    @Description("Verify that all users have a valid email format containing @")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-012: GET /users - All emails contain @")
    public void getAllUsers_AllEmailsShouldBeValid() {
        Response response = given()
                .spec(requestSpec)
            .when()
                .get(USERS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                .extract().response();

        // 2. Response body içerisinde beklenen değer kontrolleri - email formatı
        List<User> users = response.jsonPath().getList(".", User.class);
        users.forEach(user -> {
            assertNotNull(user.getEmail(), "Email null olmamalı: " + user.getName());
            assertTrue(user.getEmail().contains("@"),
                    "Geçersiz email formatı: " + user.getEmail() + " (kullanıcı: " + user.getName() + ")");
        });

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-012 PASSED: Tüm " + users.size() + " kullanıcının email formatı geçerli. Yanıt süresi: " + responseTimeMs + "ms");
    }
}

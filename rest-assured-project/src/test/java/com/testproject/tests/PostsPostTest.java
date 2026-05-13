package com.testproject.tests;

import com.testproject.config.ApiConfig;
import com.testproject.models.Post;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for the /posts endpoint (POST requests).
 * Tests creating resources via request body (JSON).
 */
@Epic("API Regression Tests")
@Feature("Posts API - POST Operations")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostsPostTest extends ApiConfig {

    private static final String POSTS_ENDPOINT = "/posts";

    // =========================================================
    // TC-006: Create a new post with valid body
    // =========================================================
    @Test
    @Order(1)
    @Story("Create new post")
    @Description("Verify that POST /posts with a valid JSON body returns 201 Created and the created resource")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("TC-006: POST /posts - Creates post and returns 201")
    public void createPost_WithValidBody_ShouldReturn201() {
        // Request body hazırlama
        Post newPost = new Post(1, "Yazılım Test Mühendisliği", "Bu test REST Assured ile yazılmıştır.");

        Response response = given()
                .spec(requestSpec)
                .body(newPost)
            .when()
                .post(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü - 201 Created bekleniyor
                .statusCode(201)
                // 2. Response body içerisinde beklenen değer kontrolleri
                .body("id", notNullValue())
                .body("title", equalTo(newPost.getTitle()))
                .body("body", equalTo(newPost.getBody()))
                .body("userId", equalTo(newPost.getUserId()))
                .extract().response();

        // Oluşturulan kaynağın ID'si dönmeli
        Post createdPost = response.as(Post.class);
        assertNotNull(createdPost.getId(), "Oluşturulan post'un ID'si null olmamalı");
        assertEquals(newPost.getTitle(), createdPost.getTitle(), "Title eşleşmeli");
        assertEquals(newPost.getBody(), createdPost.getBody(), "Body eşleşmeli");
        assertEquals(newPost.getUserId(), createdPost.getUserId(), "UserId eşleşmeli");

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-006 PASSED: Post oluşturuldu. ID=" + createdPost.getId() + ". Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-007: Create a post with all fields populated
    // =========================================================
    @Test
    @Order(2)
    @Story("Create new post - full data")
    @Description("Verify that POST /posts with all fields returns correct data in response")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-007: POST /posts - All fields reflected in response")
    public void createPost_AllFieldsReflectedInResponse() {
        String expectedTitle = "Otomasyon Test Projesi";
        String expectedBody  = "Rest Assured kullanarak otomatik API testi yazıyoruz.";
        int    expectedUserId = 5;

        Post newPost = new Post(expectedUserId, expectedTitle, expectedBody);

        Response response = given()
                .spec(requestSpec)
                .body(newPost)
            .when()
                .post(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(201)
                // 2. Response body içerisinde beklenen değer kontrolleri
                .body("title",  equalTo(expectedTitle))
                .body("body",   equalTo(expectedBody))
                .body("userId", equalTo(expectedUserId))
                .body("id",     notNullValue())
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-007 PASSED: Tüm alanlar doğrulandı. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-008: Create post with raw JSON body
    // =========================================================
    @Test
    @Order(3)
    @Story("Create post with raw JSON")
    @Description("Verify POST /posts works correctly when sending raw JSON string as body")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-008: POST /posts - Raw JSON body creates post correctly")
    public void createPost_WithRawJsonBody_ShouldReturn201() {
        String rawJsonBody = """
                {
                    "userId": 3,
                    "title": "Rest Assured ile API Testi",
                    "body": "Bu test ham JSON body kullanarak yazılmıştır."
                }
                """;

        Response response = given()
                .spec(requestSpec)
                .body(rawJsonBody)
            .when()
                .post(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(201)
                // 2. Response body içerisinde beklenen değer kontrolleri
                .body("userId", equalTo(3))
                .body("title",  equalTo("Rest Assured ile API Testi"))
                .body("id",     notNullValue())
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-008 PASSED: Ham JSON body ile post oluşturuldu. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-009: Create post - validate response Content-Type
    // =========================================================
    @Test
    @Order(4)
    @Story("Validate POST response headers")
    @Description("Verify that POST /posts response returns application/json content type")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("TC-009: POST /posts - Response Content-Type is application/json")
    public void createPost_ResponseContentType_ShouldBeJson() {
        Post newPost = new Post(2, "Header Test Postu", "Content-Type doğrulama testi.");

        Response response = given()
                .spec(requestSpec)
                .body(newPost)
            .when()
                .post(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(201)
                // 2. Content-Type header kontrolü
                .contentType(containsString("application/json"))
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-009 PASSED: POST response Content-Type doğrulandı. Yanıt süresi: " + responseTimeMs + "ms");
    }
}

package com.testproject.tests;

import com.testproject.config.ApiConfig;
import com.testproject.models.Post;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for the /posts endpoint (GET requests).
 * Uses JSONPlaceholder as the target API: https://jsonplaceholder.typicode.com
 */
@Epic("API Regression Tests")
@Feature("Posts API - GET Operations")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostsGetTest extends ApiConfig {

    private static final String POSTS_ENDPOINT = "/posts";

    // =========================================================
    // TC-001: Get all posts
    // =========================================================
    @Test
    @Order(1)
    @Story("Get all posts")
    @Description("Verify that GET /posts returns 200 OK, a non-empty list, and responds within time limit")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("TC-001: GET /posts - Returns 200 and non-empty list")
    public void getAllPosts_ShouldReturn200AndNonEmptyList() {
        Response response = given()
                .spec(requestSpec)
            .when()
                .get(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                .extract().response();

        // 2. Response body içerisinde beklenen değer kontrolleri
        List<Post> posts = response.jsonPath().getList(".", Post.class);
        assertNotNull(posts, "Post listesi null olmamalı");
        assertFalse(posts.isEmpty(), "Post listesi boş olmamalı");
        assertEquals(100, posts.size(), "JSONPlaceholder 100 post döndürmeli");

        // İlk post'un zorunlu alanları dolu mu?
        Post firstPost = posts.get(0);
        assertNotNull(firstPost.getId(), "Post ID null olmamalı");
        assertNotNull(firstPost.getTitle(), "Post title null olmamalı");
        assertNotNull(firstPost.getBody(), "Post body null olmamalı");
        assertNotNull(firstPost.getUserId(), "Post userId null olmamalı");

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-001 PASSED: " + posts.size() + " post alındı. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-002: Get a single post by ID
    // =========================================================
    @Test
    @Order(2)
    @Story("Get single post")
    @Description("Verify that GET /posts/{id} returns correct post with all expected fields")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-002: GET /posts/1 - Returns correct post data")
    public void getPostById_ShouldReturnCorrectPost() {
        int postId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
            .when()
                .get(POSTS_ENDPOINT + "/{id}")
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                // 2. Response body içerisinde beklenen değer kontrolleri
                .body("id", equalTo(postId))
                .body("userId", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()))
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        Post post = response.as(Post.class);
        assertEquals(postId, post.getId(), "Post ID eşleşmeli");
        assertEquals(1, post.getUserId(), "Post userId 1 olmalı");

        System.out.println("✅ TC-002 PASSED: Post ID=" + post.getId() + " alındı. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-003: Get posts filtered by userId
    // =========================================================
    @Test
    @Order(3)
    @Story("Filter posts by userId")
    @Description("Verify that GET /posts?userId=1 returns only posts belonging to user 1")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-003: GET /posts?userId=1 - Returns only posts for that user")
    public void getPostsByUserId_ShouldReturnFilteredPosts() {
        int userId = 1;

        Response response = given()
                .spec(requestSpec)
                .queryParam("userId", userId)
            .when()
                .get(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                .extract().response();

        // 2. Response body içerisinde beklenen değer kontrolleri
        List<Post> posts = response.jsonPath().getList(".", Post.class);
        assertFalse(posts.isEmpty(), "Filtrelenmiş post listesi boş olmamalı");

        // Tüm post'lar userId=1'e ait olmalı
        posts.forEach(post ->
            assertEquals(userId, post.getUserId(),
                    "Tüm post'lar userId=" + userId + "'e ait olmalı, bulunan: " + post.getUserId())
        );

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-003 PASSED: userId=" + userId + " için " + posts.size() + " post bulundu. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-004: Get non-existent post returns 404
    // =========================================================
    @Test
    @Order(4)
    @Story("Handle not found post")
    @Description("Verify that requesting a non-existent post ID returns 404")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-004: GET /posts/9999 - Returns 404 for non-existent post")
    public void getPostById_WithInvalidId_ShouldReturn404() {
        Response response = given()
                .spec(requestSpec)
                .pathParam("id", 9999)
            .when()
                .get(POSTS_ENDPOINT + "/{id}")
            .then()
                // 1. Status code kontrolü - 404 bekleniyor
                .statusCode(404)
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-004 PASSED: 404 status kodu alındı. Yanıt süresi: " + responseTimeMs + "ms");
    }

    // =========================================================
    // TC-005: Validate Content-Type header
    // =========================================================
    @Test
    @Order(5)
    @Story("Validate response headers")
    @Description("Verify that the API returns JSON content type in response headers")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("TC-005: GET /posts - Response Content-Type is application/json")
    public void getAllPosts_ShouldReturnJsonContentType() {
        Response response = given()
                .spec(requestSpec)
            .when()
                .get(POSTS_ENDPOINT)
            .then()
                // 1. Status code kontrolü
                .statusCode(200)
                // 2. Content-Type header kontrolü
                .contentType(containsString("application/json"))
                .extract().response();

        // 3. x süre altında cevap dönüldüğünün kontrolü
        long responseTimeMs = response.time();
        assertTrue(responseTimeMs < MAX_RESPONSE_TIME_MS,
                "Yanıt süresi " + MAX_RESPONSE_TIME_MS + "ms altında olmalı, fakat: " + responseTimeMs + "ms");

        System.out.println("✅ TC-005 PASSED: Content-Type doğrulandı. Yanıt süresi: " + responseTimeMs + "ms");
    }
}

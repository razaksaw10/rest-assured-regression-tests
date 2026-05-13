package com.testproject.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model class representing a Post resource from JSONPlaceholder API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    private Integer id;
    private Integer userId;
    private String title;
    private String body;

    // Default constructor (required for Jackson deserialization)
    public Post() {}

    // Constructor for creating new posts (POST request)
    public Post(Integer userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", userId=" + userId +
               ", title='" + title + "', body='" + body + "'}";
    }
}

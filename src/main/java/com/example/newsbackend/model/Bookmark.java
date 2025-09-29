package com.example.newsbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "articleUrl"}))
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String articleUrl;

    private String title;

    private String description;

    private String urlToImage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getArticleUrl() { return articleUrl; }
    public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrlToImage() { return urlToImage; }
    public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
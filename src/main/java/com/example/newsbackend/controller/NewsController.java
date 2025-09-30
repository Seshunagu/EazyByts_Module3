package com.example.newsbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Value("${newsapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public NewsController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/top-headlines")
    public ResponseEntity<?> getTopHeadlines(
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "general") String category,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page) {
        try {
            String url = String.format(
                "https://newsapi.org/v2/top-headlines?country=%s&category=%s&pageSize=%d&page=%d&apiKey=%s",
                country, category, pageSize, page, apiKey);
            logger.info("Fetching NewsAPI: {}", url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            logger.info("NewsAPI response: status={}, articles={}", 
                response.getBody().get("status"), 
                ((List<?>) response.getBody().getOrDefault("articles", Collections.emptyList())).size());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Failed to fetch top headlines: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to fetch news",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getNews(
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "5") int pageSize) {
        try {
            String url = String.format(
                "https://newsapi.org/v2/top-headlines?country=%s&pageSize=%d&apiKey=%s",
                country, pageSize, apiKey);
            logger.info("Fetching NewsAPI: {}", url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            logger.info("NewsAPI response: status={}, articles={}", 
                response.getBody().get("status"), 
                ((List<?>) response.getBody().getOrDefault("articles", Collections.emptyList())).size());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Failed to fetch news: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to fetch news",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/everything")
    public ResponseEntity<?> getEverything(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page) {
        try {
            String url = String.format(
                "https://newsapi.org/v2/everything?q=%s&pageSize=%d&page=%d&apiKey=%s",
                URLEncoder.encode(q, StandardCharsets.UTF_8), pageSize, page, apiKey);
            logger.info("Fetching NewsAPI: {}", url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            logger.info("NewsAPI response: status={}, articles={}", 
                response.getBody().get("status"), 
                ((List<?>) response.getBody().getOrDefault("articles", Collections.emptyList())).size());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Failed to fetch everything: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to fetch news",
                "message", e.getMessage()
            ));
        }
    }
}

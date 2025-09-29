package com.example.newsbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@CrossOrigin(origins = "https://seshu-eazybyts-module3.onrender.com") // Allow frontend origin
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Value("${newsapi.key}")
    private String newsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Top headlines endpoint
    @GetMapping("/top-headlines")
    public ResponseEntity<?> getTopHeadlines(
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "general") String category,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page) {

        String url = String.format(
                "https://newsapi.org/v2/top-headlines?country=%s&category=%s&pageSize=%d&page=%d&apiKey=%s",
                country, category, pageSize, page, newsApiKey
        );

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.TooManyRequests e) {
            return ResponseEntity.status(429).body(Map.of("message", "Rate limit exceeded. Try again later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch news: " + e.getMessage()));
        }
    }

    // Everything endpoint
    @GetMapping("/everything")
    public ResponseEntity<?> getEverything(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page) {

        String url = String.format(
                "https://newsapi.org/v2/everything?q=%s&pageSize=%d&page=%d&apiKey=%s",
                q, pageSize, page, newsApiKey
        );

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.TooManyRequests e) {
            return ResponseEntity.status(429).body(Map.of("message", "Rate limit exceeded. Try again later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch news: " + e.getMessage()));
        }
    }

    // Trending news endpoint
    @GetMapping
    public ResponseEntity<?> getTrending(
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "5") int pageSize) {

        String url = String.format(
                "https://newsapi.org/v2/top-headlines?country=%s&pageSize=%d&apiKey=%s",
                country, pageSize, newsApiKey
        );

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.TooManyRequests e) {
            return ResponseEntity.status(429).body(Map.of("message", "Rate limit exceeded. Try again later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch trending news: " + e.getMessage()));
        }
    }
}

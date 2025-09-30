package com.example.newsbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5500",
        "https://seshu-eazybyts-module3.onrender.com"
})
@RestController
@RequestMapping("/api/news")
public class NewsController {

    // Example: /api/news/top-headlines?country=us&category=general&pageSize=20&page=1
    @GetMapping("/top-headlines")
    public ResponseEntity<?> getTopHeadlines(@RequestParam(defaultValue = "us") String country,
                                             @RequestParam(defaultValue = "general") String category,
                                             @RequestParam(defaultValue = "20") int pageSize,
                                             @RequestParam(defaultValue = "1") int page) {
        // TODO: Replace with actual service that fetches news from DB or external API
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Top headlines fetched successfully");
        response.put("country", country);
        response.put("category", category);
        response.put("pageSize", pageSize);
        response.put("page", page);

        return ResponseEntity.ok(response);
    }

    // Example: /api/news?country=us&pageSize=5   (Trending section)
    @GetMapping
    public ResponseEntity<?> getTrendingNews(@RequestParam(defaultValue = "us") String country,
                                             @RequestParam(defaultValue = "5") int pageSize) {
        // TODO: Replace with actual trending logic
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Trending news fetched successfully");
        response.put("country", country);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}

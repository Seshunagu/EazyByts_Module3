package com.example.newsbackend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.newsbackend.model.Bookmark;
import com.example.newsbackend.model.User;
import com.example.newsbackend.repository.BookmarkRepository;
import com.example.newsbackend.repository.UserRepository;
import com.example.newsbackend.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Add a bookmark
    @PostMapping("")
    public ResponseEntity<?> addBookmark(@RequestBody BookmarkRequest bookmarkRequest, HttpServletRequest request) {
        try {
            User user = getUserFromRequest(request);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            String normalizedUrl = bookmarkRequest.getUrl().toLowerCase();
            boolean exists = bookmarkRepository.existsByUserAndArticleUrl(user, normalizedUrl);
            if (exists) {
                return ResponseEntity.status(409).body(Map.of("error", "Bookmark already exists for this article"));
            }

            Bookmark bookmark = new Bookmark();
            bookmark.setArticleUrl(normalizedUrl);
            bookmark.setTitle(bookmarkRequest.getTitle());
            bookmark.setDescription(bookmarkRequest.getDescription());
            bookmark.setUrlToImage(bookmarkRequest.getUrlToImage());
            bookmark.setUser(user);

            bookmarkRepository.save(bookmark);
            return ResponseEntity.ok(Map.of("message", "Bookmark added successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: Unable to add bookmark"));
        }
    }

    // List bookmarks for logged-in user
    @GetMapping("")
    public ResponseEntity<?> getBookmarks(HttpServletRequest request) {
        try {
            User user = getUserFromRequest(request);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            List<Bookmark> bookmarks = bookmarkRepository.findByUser(user);
            List<BookmarkResponse> response = bookmarks.stream().map(b -> new BookmarkResponse(
                    b.getArticleUrl(),
                    b.getTitle(),
                    b.getDescription(),
                    b.getUrlToImage()
            )).toList();

            return ResponseEntity.ok(Map.of("bookmarks", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: Unable to fetch bookmarks"));
        }
    }

    // Remove a bookmark
    @DeleteMapping("")
    @Transactional
    public ResponseEntity<?> removeBookmark(@RequestParam String url, HttpServletRequest request) {
        try {
            User user = getUserFromRequest(request);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            String normalizedUrl = url.toLowerCase();
            boolean exists = bookmarkRepository.existsByUserAndArticleUrl(user, normalizedUrl);
            if (!exists) {
                return ResponseEntity.status(404).body(Map.of("error", "Bookmark not found"));
            }

            bookmarkRepository.deleteByUserAndArticleUrl(user, normalizedUrl);
            return ResponseEntity.ok(Map.of("message", "Bookmark removed successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: Unable to remove bookmark"));
        }
    }

    // Utility: get user from JWT token
  private User getUserFromRequest(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    String token = header.substring(7);
    if (!jwtUtil.validateJwtToken(token)) {
        throw new RuntimeException("Invalid or expired token");
    }

    Long userId = jwtUtil.getUserIdFromJwt(token);
    return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
}


    // DTO for request
    public static class BookmarkRequest {
        private String url;
        private String title;
        private String description;
        private String urlToImage;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getUrlToImage() { return urlToImage; }
        public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }
    }

    // DTO for response
    public static class BookmarkResponse {
        private String url;
        private String title;
        private String description;
        private String urlToImage;

        public BookmarkResponse(String url, String title, String description, String urlToImage) {
            this.url = url;
            this.title = title;
            this.description = description;
            this.urlToImage = urlToImage;
        }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getUrlToImage() { return urlToImage; }
        public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }
    }
}

package com.example.newsbackend.controller;

import com.example.newsbackend.model.User;
import com.example.newsbackend.security.JwtUtil;
import com.example.newsbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String email = body.get("email");
            String password = body.get("password");
            String preferredCategory = body.get("preferredCategory");
            if (username == null || email == null || password == null || preferredCategory == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }
            User saved = authService.register(username, email, password, preferredCategory);
            return ResponseEntity.ok(Map.of("message", "Registration successful", "username", saved.getUsername()));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }
        var opt = authService.authenticate(email, password);
        if (opt.isPresent()) {
            User u = opt.get();
            String token = jwtUtil.generateToken(u.getEmail(), u.getId());
            return ResponseEntity.ok(Map.of("token", token, "username", u.getUsername(), "preferredCategory", u.getPreferredCategory()));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}

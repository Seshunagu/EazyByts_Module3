package com.example.newsbackend.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // ===================== Password Encoder =====================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ===================== Global CORS Filter (optional extra layer) =====================
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        logger.info("Initializing global CORS filter...");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5500",
            "https://seshu-eazybyts-module3.onrender.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        logger.info("CORS filter registered for all endpoints with allowed origins: {}", config.getAllowedOrigins());
        return new CorsFilter(source);
    }

    // ===================== Security Filter Chain =====================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Building security filter chain with proper CORS handling...");

        http
            // Enable CORS in Spring Security
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(
                    "http://localhost:3000",
                    "http://localhost:5500",
                    "https://seshu-eazybyts-module3.onrender.com"
                ));
                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            // Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())
            // Stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Authorize requests
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/news/**").permitAll()
                .requestMatchers("/", "/index.html", "/favicon.ico", "/static/**").permitAll()
                .anyRequest().authenticated()
            );

        logger.info("Security filter chain configured successfully");
        return http.build();
    }

    // ===================== Logging Filter for debugging =====================
    @Bean
    public Filter logRequestsFilter() {
        return (request, response, chain) -> {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            logger.info("[REQUEST] {} {} from {}", req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
            chain.doFilter(request, response);
            logger.info("[RESPONSE] {} {} - Status: {}", req.getMethod(), req.getRequestURI(), res.getStatus());
        };
    }
}

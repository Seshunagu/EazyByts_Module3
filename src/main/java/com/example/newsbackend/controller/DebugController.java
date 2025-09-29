package com.example.newsbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/debug")
    public String debug(HttpServletRequest request, @RequestHeader(value = "Origin", required = false) String origin) {
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Origin Header: " + origin);
        System.out.println("Headers: " + request.getHeaderNames().asIterator()
            .forEachRemaining(name -> System.out.println(name + ": " + request.getHeader(name))));
        return "Debug: Received request from " + origin;
    }
}

package com.tracker.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.server.dto.AdminLoginRequest;
import com.tracker.server.dto.LoginRequest;
import com.tracker.server.dto.LoginResponse;
import com.tracker.server.dto.RegisterRequest;
import com.tracker.server.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/register")
    public String adminRegister(
            @RequestBody AdminLoginRequest request) {

        return authService.adminRegister(
                request);
    }
    
    

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        try {

            String response = authService.register(request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            if ("Username already registered".equals(e.getMessage())) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Username already registered");
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    
    
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        System.out.println("LOGIN API HIT");

        return authService.login(request);
    }
    
    
    
    @PostMapping("/admin")
    public LoginResponse adminLogin(
            @RequestBody AdminLoginRequest request) {

        System.out.println("LOGIN API HIT");

        return authService.AdminLogin(request);
    }
    
    
}
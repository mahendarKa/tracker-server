package com.tracker.server.controller;

import org.springframework.web.bind.annotation.*;

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
    public String register(
            @RequestBody RegisterRequest request) {

        return authService.register(
                request);
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
package com.tracker.server.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tracker.server.dto.AdminLoginRequest;
import com.tracker.server.dto.LoginRequest;
import com.tracker.server.dto.LoginResponse;
import com.tracker.server.dto.RegisterRequest;
import com.tracker.server.entity.User;
import com.tracker.server.repository.UserRepository;
import com.tracker.server.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public String register(
            RegisterRequest request) {
    

        User user =
                User.builder()
                        .username(
                                request.getUsername())
//                        .password(
//                                passwordEncoder.encode(
//                                        request.getPassword()))
//                        .role(
//                                request.getRole()

                        .role(
                                "USER")
                        .build();

        userRepository.save(user);

        return "User Registered";
    }
    
    
    
    public String adminRegister(
            AdminLoginRequest request) {
    

        User user =
                User.builder()
                        .username(
                                request.getUsername())
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()))
                       

                        .role(
                                "ADMIN")
                        .build();

        userRepository.save(user);

        return "admin Registered";
    }

    public LoginResponse login(
            LoginRequest request) {

//        User user =
//                userRepository.findByUsername(
//                                request.getUsername())
//                        .orElseThrow();
    	System.out.println("LOGIN USERNAME = " + request.getUsername());

    	userRepository.findAll().forEach(
    	    u -> System.out.println(
    	        "DB USER = " + u.getUsername()
    	    )
    	);
    	User user =
    	        userRepository.findByUsername(
    	                request.getUsername())
    	        .orElseThrow(() ->
    	                new RuntimeException(
    	                        "User Not Found : "
    	                        + request.getUsername()));

//        if (!passwordEncoder.matches(
//                request.getPassword(),
//                user.getPassword())) {
//
//            throw new RuntimeException(
//                    "Invalid Credentials");
//        }

        String token =
                jwtUtil.generateToken(
                        user.getUsername(),user.getId());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(
                        user.getUsername())
                .role(user.getRole())
                .build();
    }
    
    
    public LoginResponse AdminLogin(
            AdminLoginRequest request) {

        User user =
                userRepository.findByUsername(
                                request.getUsername())
                .orElseThrow(() ->
                new RuntimeException(
                        "User Not Found : "
                        + request.getUsername()));
    	log.info("LOGIN USERNAME = {}" , request.getUsername());

    	userRepository.findAll().forEach(
    	    u -> log.info(
    	        "DB USER = {}" ,u.getUsername()
    	    )
    	);
    

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid Credentials");
        }

        String token =
                jwtUtil.generateToken(
                        user.getUsername(),user.getId());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(
                        user.getUsername())
                .role(user.getRole())
                .build();
    }
}
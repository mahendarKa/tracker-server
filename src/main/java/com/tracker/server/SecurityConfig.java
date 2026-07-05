//package com.tracker.server;
//
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.web.SecurityFilterChain;
////
//////
//////import org.springframework.context.annotation.Bean;
//////import org.springframework.context.annotation.Configuration;
//////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//////import org.springframework.security.crypto.password.PasswordEncoder;
//////import org.springframework.security.web.SecurityFilterChain;
//////
//////@Configuration
//////public class SecurityConfig {
//////
//////    @Bean
//////    public SecurityFilterChain securityFilterChain(
//////            HttpSecurity http)
//////            throws Exception {
//////
//////        http
//////                .csrf(csrf -> csrf.disable())
//////                .authorizeHttpRequests(auth -> auth
//////                        .requestMatchers(
////////                                "/api/auth/**")
//////                        		  "/api/auth/**",
//////                                  "/api/device/**",
//////                                  "/api/process/**",
//////                                  "/api/idle/**",
//////                                  "/api/window/**")
//////                        .permitAll()
//////                        .anyRequest()
//////                        .authenticated());
////////                .httpBasic(Customizer.withDefaults());
//////
//////        return http.build();
//////    }
//////
//////    @Bean
//////    public PasswordEncoder passwordEncoder() {
//////
//////        return new BCryptPasswordEncoder();
//////    }
//////}
////
////
////@Configuration
////public class SecurityConfig {
////
////    public SecurityConfig() {
////        System.out.println("SECURITY CONFIG LOADED");
////    }
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(
////            HttpSecurity http)
////            throws Exception {
////
////        http
////                .csrf(csrf -> csrf.disable())
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(
////                                "/api/auth/**",
////                                "/api/device/**",
////                                "/api/process/**",
////                                "/api/idle/**",
////                                "/api/window/**")
////                        .permitAll()
////                        .anyRequest()
////                        .authenticated());
////
////        return http.build();
////    }
////}
//
//
//
////@Configuration
////public class SecurityConfig {
////
////    public SecurityConfig() {
////        System.out.println("SECURITY CONFIG LOADED");
////    }
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(
////            HttpSecurity http)
////            throws Exception {
////
////        http
////                .csrf(csrf -> csrf.disable())
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(
////                                "/api/auth/**",
////                                "/api/device/**",
////                                "/api/process/**",
////                                "/api/idle/**",
////                                "/api/window/**")
////                        .permitAll()
////                        .anyRequest()
////                        .authenticated());
////
////        return http.build();
////    }
////}
//
////package com.tracker.server.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    public SecurityConfig() {
//        System.out.println("SECURITY CONFIG LOADED");
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http)
//            throws Exception {
//    	 System.out.println("FILTER CHAIN CREATED");
//
////        http
////                .csrf(csrf -> csrf.disable())
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(
////                                "/api/auth/**",
////                                "/api/device/**",
////                                "/api/process/**",
////                                "/api/idle/**",
////                                "/api/window/**")
////                        .permitAll()
////                        .anyRequest()
////                        .authenticated());
//    	 
//    	 http
//         .csrf(csrf -> csrf.disable())
//         .formLogin(form -> form.disable())
//         .httpBasic(httpBasic -> httpBasic.disable())
//         .authorizeHttpRequests(auth -> auth
//                 .anyRequest()
//                 .permitAll());
//
//     return http.build();
//
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}


package com.tracker.server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tracker.server.security.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
        .cors(cors -> {})
				.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers(
//                                "/api/auth/**","/api/device/register/**")
//                        .permitAll()
//
//                        .anyRequest()
//                        .authenticated())
				
				
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
					    .permitAll()

					    .requestMatchers(
					        "/api/auth/**",
					        "/api/device/register/**",
					        "/api/process/**",
					        "/api/idle/**",
					        "/api/window/**",
					        "/api/session/**",
					        "/api/test/**"
					    )
					    .permitAll()

					    .anyRequest()
					    .authenticated()
					)

				.httpBasic(h -> h.disable())
				.formLogin(form -> form.disable());

        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
    
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
}
}
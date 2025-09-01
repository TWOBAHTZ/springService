package com.example.springservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth // ✅/commissions
                        .requestMatchers(HttpMethod.OPTIONS, "/auth/**","/user/**","/admin/**","/posts/**","/guest/**" ,"/commissions/**","/artist/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/**","/user/**","/admin/**","/posts/**","/reports/**" , "/notifications/**","/commissions/**","/artist/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/**","/user/**","/admin/**","/posts/**" ,"/guest/**" ,"/notifications/**","/commissions/**","/artist/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/auth/**","/user/**","/admin/**","/posts/**","/commissions/**","/artist/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/auth/**","/user/**","/admin/**","/posts/**","/notifications/**","/commissions/**","/artist/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/auth/**","/user/**","/admin/**","/posts/**","/commissions/**","/artist/**").permitAll()
//                        .requestMatchers(HttpMethod.HEAD, "/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));// ✅
        config.setAllowedMethods(List.of("GET", "POST", "PUT","PATCH","HEAD", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}

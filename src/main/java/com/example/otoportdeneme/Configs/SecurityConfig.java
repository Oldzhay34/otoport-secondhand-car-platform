package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.services.CustomUserDetailsService;
import com.example.otoportdeneme.services.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain chain(HttpSecurity http,
                                     JwtService jwtService,
                                     CustomUserDetailsService uds) throws Exception {

        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService, uds);

        http.csrf(csrf -> csrf.disable());

        // JWT -> stateless
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Default login ekranı kapalı
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // Yetkisiz istekte 401
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(
                (req, res, e) -> res.sendError(401, "Unauthorized")
        ));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/favicon.ico",
                        "/templates/**",
                        "/css/**", "/js/**",
                        "/images/**", "/imagesforapp/**",
                        "/uploads/**",
                        "/filejson/**"
                ).permitAll()

                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/home/**").permitAll()
                .requestMatchers("/api/stores/**").permitAll()
                .requestMatchers("/api/listings/**").permitAll()
                .requestMatchers("/api/visit/**").permitAll()

                // ✅ Inquiry: guest YAZAMASIN
                // - GET okumalar store/client tarafında zaten ayrı endpointlerde
                // - vehicleinfo'dan mesaj atacaksa client login olmalı
                .requestMatchers("/api/inquiries/**").hasRole("CLIENT")

                .requestMatchers("/api/client/**").hasRole("CLIENT")
                .requestMatchers("/api/store/**").hasRole("STORE")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

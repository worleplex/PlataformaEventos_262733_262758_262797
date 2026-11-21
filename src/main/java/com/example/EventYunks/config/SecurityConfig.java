package com.example.EventYunks.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtro(HttpSecurity http) throws Exception {
        http.csrf(csrf -> {}).formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(new FiltroSesionSecurityConfig(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/registro", "/index.html"
                        ).permitAll()
                        // Las rutas del admin
                        .requestMatchers(
                                "/admin", "/admin/**"
                        ).hasRole("ADMIN")
                        //Las rutas del organizador
                        .requestMatchers(
                                "/organizador", "/organizador/**"
                        ).hasRole("ORGANIZADOR")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, error) ->
                                response.sendRedirect("/login")
                        ).accessDeniedHandler((request, response, error) -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
                );
        return http.build();
    }
}

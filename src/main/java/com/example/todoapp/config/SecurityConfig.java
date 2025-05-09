package com.example.todoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] GET_AUTH_WHITELIST = {
            // SWAGGER
            "/*",
            "/swagger-ui/**",
            "/v3/**",
            // Actuator
            "/actuator/"
    };

    @Bean
    public SecurityFilterChain configureNoAuthentication(final HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        requests -> requests.requestMatchers(HttpMethod.GET, GET_AUTH_WHITELIST)
                                .permitAll()
                                .requestMatchers("/api/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated());
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package org.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class securityconfig{

    @Bean//WHAT IS A BEAN ? SO THE BEAN IS BASICALLY THE OBJECT WHICH IS USED OT MANAGED AND MAINTAINED BY SPRING FRAMEWORK INSTEAD OF THE MANUAL CODE
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/tourist/send-otp").permitAll()
                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
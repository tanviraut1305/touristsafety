package org.example.demo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class jwtautenticator extends OncePerRequestFilter {

    // IMPORTANT:
    // This secret must be exactly the same as the secret
    // used in jwtservice.java
    private static final String SECRET =
            "MySuperSecretKeyForTouristSafetyApplication123456789";

    // Use SecretKey, NOT java.security.Key
    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // No JWT provided
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check Bearer format
        if (!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token = authHeader.substring(7).trim();

        // Empty token
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Validate and parse JWT
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Get mobile number from JWT subject
            String mobileNumber = claims.getSubject();

            // Make sure mobile number exists
            if (mobileNumber != null && !mobileNumber.isBlank()) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                mobileNumber,
                                null,
                                Collections.emptyList()
                        );

                // Store authentication in Spring Security
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            // Invalid JWT
            SecurityContextHolder.clearContext();

            System.out.println(
                    "Invalid JWT Token: " + e.getMessage()
            );
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}
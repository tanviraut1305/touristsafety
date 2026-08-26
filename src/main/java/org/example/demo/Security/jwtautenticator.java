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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

@Component
public class jwtautenticator extends OncePerRequestFilter {

    // IMPORTANT:
    // This secret MUST be exactly the same secret used
    // when creating/signing the JWT in your login service.
    private static final String SECRET =
            "MySuperSecretKeyForTouristSafetyApplication123456789";

    private final Key key =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No JWT provided.
        // Allow the request to continue.
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization header exists but is not Bearer format.
        if (!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        // Empty token
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String mobileNumber = claims.getSubject();

            // Make sure subject exists
            if (mobileNumber != null && !mobileNumber.isBlank()) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                mobileNumber,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            // Remove invalid authentication
            SecurityContextHolder.clearContext();

            System.out.println(
                    "Invalid JWT Token: " + e.getMessage()
            );
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}
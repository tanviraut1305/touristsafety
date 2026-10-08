package org.example.demo.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.demo.Entity.Tourist;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class jwtservice {

    // IMPORTANT:
    // This must be exactly the same secret
    // used in jwtautenticator.java
    private static final String SECRET =
            "MySuperSecretKeyForTouristSafetyApplication123456789";

    // Use SecretKey
    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    // Generate JWT
    public String generateToken(Tourist tourist) {

        return Jwts.builder()
                .subject(tourist.getMobileNumber())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60 * 24
                        )
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
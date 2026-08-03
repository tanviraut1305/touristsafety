package org.example.demo.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.demo.Entity.Tourist;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class jwtservice{

    // Secret key for signing the JWT
    private static final String SECRET =
            "MySuperSecretKeyForTouristSafetyApplication123456789";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Tourist tourist) {

        return Jwts.builder()
                .subject(tourist.getMobileNumber())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

}
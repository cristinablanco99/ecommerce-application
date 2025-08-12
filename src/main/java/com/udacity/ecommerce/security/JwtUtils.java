package com.udacity.ecommerce.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration-minutes}")
    private long expirationMinutes;

    private Algorithm algorithm;

    @PostConstruct
    void init() {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    public String validateAndGetSubject(String token) {
        return JWT.require(algorithm).withIssuer(issuer).build()
                .verify(token).getSubject();
    }
}

package org.mirea.pm.notes_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${notes.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private final SecretKeySpec jwtSecret;

    public JwtUtils(
            @Value("${notes.app.keyAlgorithm}") final String keyAlgorithm,
            @Value("${notes.app.jwtSecret}") final String jwtSecretString
    ){
        try {
            jwtSecret = new SecretKeySpec(
                    jwtSecretString.getBytes(StandardCharsets.UTF_8),
                    keyAlgorithm
            );
        }
        catch (Exception e) {
            logger.error("Failed to construct JwtUtils, probably property variables missing: {}", e.getMessage());
            throw new MissingEnvironmentVariableException(
                    "Environment variable not found or dependency injection failed: " + e);
        }
    }

    public String generateJwtToken(@NotNull Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Date currentDateTime = new Date();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(currentDateTime)
                .setExpiration(new Date(currentDateTime.getTime() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    public String generateJwtToken(String username) {
        Date currentDateTime = new Date();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDateTime)
                .setExpiration(new Date(currentDateTime.getTime() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.
                parserBuilder().
                setSigningKey(jwtSecret).
                build().
                parseClaimsJws(token).
                getBody().
                getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}

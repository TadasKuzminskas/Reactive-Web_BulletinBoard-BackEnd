package com.example.demo2.config.security.jwt;

import com.example.demo2.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private String secret = "IfYouComeUpWithAVeryCleverSecretYouAreWelcomeToPutItHere";
    private String refreshSecret = "AnotherLineOfDummyTextToSecureTheRefreshTokenTheOddsOfItBeingGuessedAreAstronomical";
    private String expireTimeMilisec = "3000";

    public String generateToken(User user) {
        Date now = new Date();
        Map<String, Object> claim = new HashMap<>();
        claim.put("alg", "HS256");
        claim.put("typ", "JWT");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(expireTimeMilisec) * 1000))
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .setHeaderParams(claim)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Map<String, Object> claim = new HashMap<>();
        claim.put("alg", "HS256");
        claim.put("typ", "JWT");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(expireTimeMilisec) * 100000))
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(refreshSecret.getBytes()))
                .setHeaderParams(claim)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }


    public Date getExpirationDate(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public Claims getClaimsFromRefreshToken(String token) {
        return Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(refreshSecret.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenValidated(String token) {
        return !isTokenExpired(token);
    }

    public Date getRefreshExpirationDate(String token) {
        return getClaimsFromRefreshToken(token).getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date());
    }
    public Boolean isRefreshTokenExpired(String token) {
        Date expirationDate = getRefreshExpirationDate(token);
        return expirationDate.before(new Date());
    }

    public Boolean isRefreshTokenValidated(String token) {
        return !isRefreshTokenExpired(token);
    }

    public String getUsernameFromRefreshToken(String token) {
        return getClaimsFromRefreshToken(token).getSubject();
    }

}

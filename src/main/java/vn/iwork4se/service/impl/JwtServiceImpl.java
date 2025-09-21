package vn.iwork4se.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.TokenType;
import vn.iwork4se.exception.InvalidDataException;
import vn.iwork4se.service.JwtService;


import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static vn.iwork4se.common.TokenType.ACESS_TOKEN;
import static vn.iwork4se.common.TokenType.REFRESH_TOKEN;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.expiryMinutes}")
    private Long expiryMinutes;
    @Value("${jwt.expiryDays}")
    private Long expiryDays;
    @Value("${jwt.accessKey}")
    private String accessKey;
    @Value("${jwt.refreshKey}")
    String refreshKey;

    @Override
    public String generateAccessToken(String userId, String userName, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generating access token for userId: {}, userName: {}, authorities: {}", userId, userName, authorities);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);


        return generateAccessToken(claims, userName);
    }

    @Override
    public String generateRefreshToken(String userId, String userName, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generating refresh token for userId: {}, userName: {}, authorities: {}", userId, userName,authorities);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);


        return generateRefreshToken(claims, userName);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        log.info("Extracting username from token: {}", token);
        return extractClaim(token, type, Claims::getSubject);
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
        log.info("----------[ extractClaim ]----------");
        final Claims claims = extractAllClaims(token, type);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token, TokenType type) {
        log.info("----------[ extraAllClaim ]----------");
        try {
            return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }

    private String generateAccessToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*expiryMinutes))
                .signWith(getKey(ACESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }
    private String generateRefreshToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*expiryDays))
                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {
        switch (type) {
            case ACESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default ->throw new InvalidDataException("Invalid token type");
        }

    }
}

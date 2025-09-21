package vn.iwork4se.service;

import org.springframework.security.core.GrantedAuthority;
import vn.iwork4se.common.TokenType;

import java.util.Collection;

public interface JwtService {
    String generateAccessToken(String userId, String userName, Collection<? extends GrantedAuthority> authorities);
    String generateRefreshToken(String userId, String userName, Collection<? extends GrantedAuthority> authorities);
    String extractUsername(String token, TokenType type);
}

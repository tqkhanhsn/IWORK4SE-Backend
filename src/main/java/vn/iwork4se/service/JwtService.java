package vn.iwork4se.service;

import org.springframework.security.core.GrantedAuthority;
import vn.iwork4se.common.TokenType;

import java.util.Collection;
import java.util.List;

public interface JwtService {
    String generateAccessToken(String username,String platform, List<String> authorities);

    String generateRefreshToken(String username,String platform, List<String> authorities);
    String extractUsername(String token, TokenType type);
    String extractPlatform(String token, TokenType type);
}

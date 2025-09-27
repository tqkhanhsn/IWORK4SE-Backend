package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.iwork4se.service.RefreshTokenService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "REFRESH_TOKEN_SERVICE")
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiryDays}")
    private int expiryDays;

    @Value("${jwt.key-prefix}")
    private String keyPrefix;

    private String getRedisKey(String username, String platform) {
        return keyPrefix + username+ ":" +platform;
    }

    @Override
    public void storeRefreshToken(String username,String platform, String refreshToken) {
        try {
            String key = getRedisKey(username, platform);
            redisTemplate.opsForValue().set(key, refreshToken, expiryDays, TimeUnit.DAYS);
            log.info("Stored refresh token for user: {}", username);
        } catch (Exception e) {
            log.error("Error storing refresh token for user: {}, error: {}", username, e.getMessage());
            throw new RuntimeException("Failed to store refresh token", e);
        }
    }

    @Override
    public String getRefreshToken(String username, String platform) {
        try {
            String key = getRedisKey(username, platform);
            Object token = redisTemplate.opsForValue().get(key);
            return token != null ? token.toString() : null;
        } catch (Exception e) {
            log.error("Error getting refresh token for user: {}, error: {}", username, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateRefreshToken(String username, String platform, String refreshToken) {
        try {
            String storedToken = getRefreshToken(username, platform);
            boolean isValid = storedToken != null && storedToken.equals(refreshToken);
            log.info("Refresh token validation for user: {} - {}", username, isValid ? "VALID" : "INVALID");
            return isValid;
        } catch (Exception e) {
            log.error("Error validating refresh token for user: {}, error: {}", username, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteRefreshToken(String username, String platform) {
        try {
            String key = getRedisKey(username,platform);
            redisTemplate.delete(key);
            log.info("Deleted refresh token for user: {}", username);
        } catch (Exception e) {
            log.error("Error deleting refresh token for user: {}, error: {}", username, e.getMessage());
        }
    }

    @Override
    public void deleteAllRefreshTokensForUser(String username) {
        try {
            String pattern = keyPrefix + username + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Deleted {} refresh tokens for user: {}", keys.size(), username);
            }
        } catch (Exception e) {
            log.error("Error deleting all refresh tokens for user: {}, error: {}", username, e.getMessage());
        }
    }
}


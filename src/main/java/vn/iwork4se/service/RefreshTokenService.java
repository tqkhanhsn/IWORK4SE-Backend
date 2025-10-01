package vn.iwork4se.service;

public interface RefreshTokenService {
    void storeRefreshToken(String username, String platform, String refreshToken);

    String getRefreshToken(String username, String platform);

    boolean validateRefreshToken(String username, String platform, String refreshToken);

    void deleteRefreshToken(String username, String platform);

    void deleteAllRefreshTokensForUser(String username);
}

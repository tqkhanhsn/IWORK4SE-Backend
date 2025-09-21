package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iwork4se.controller.request.SignInRequest;
import vn.iwork4se.controller.response.TokenResponse;
import vn.iwork4se.exception.ForBiddenException;
import vn.iwork4se.exception.InvalidDataException;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.AuthenticationService;
import vn.iwork4se.service.JwtService;

import static vn.iwork4se.common.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get Access Token");
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
        var user = userRepository.findByUserName(request.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException(request.getUsername());
        }

        String accessToken = jwtService.generateAccessToken(user.getId(),request.getUsername(),user.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(user.getId(),request.getUsername(),user.getAuthorities());
        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();

    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Get refresh token");

        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }

        try {
            // Verify token
            String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

            // check user is active or inactivated
            User user = userRepository.findByUserName(userName);

            // generate new access token
            String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getAuthorities());

            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new ForBiddenException(e.getMessage());
        }
    }
}

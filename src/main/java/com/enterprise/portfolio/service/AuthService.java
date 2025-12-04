package com.enterprise.portfolio.service;

import com.enterprise.portfolio.dto.AuthRequest;
import com.enterprise.portfolio.dto.AuthResponse;
import com.enterprise.portfolio.dto.UserDto;
import com.enterprise.portfolio.model.RefreshToken;
import com.enterprise.portfolio.model.User;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse authenticateUser(AuthRequest authRequest);
    AuthResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    String generateJwtToken(Authentication authentication);
    String generateJwtToken(User user);
    RefreshToken createRefreshToken(Long userId);
    User getCurrentAuthenticatedUser();
    String getJwtFromRequest(HttpServletRequest request);
    boolean validateJwtToken(String authToken);
    UserDto getCurrentUser();
}

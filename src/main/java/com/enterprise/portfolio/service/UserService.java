package com.enterprise.portfolio.service;

import com.enterprise.portfolio.dto.UserDto;
import com.enterprise.portfolio.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService extends BaseService<UserDto, User, Long> {
    User findEntityById(Long id);
    UserDto findByUsername(String username);
    UserDto findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UserDto registerUser(UserDto userDto, String password);
    void changePassword(Long userId, String currentPassword, String newPassword);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    void verifyEmail(String token);
    UserDto getCurrentUser();
    Page<UserDto> searchUsers(String query, Pageable pageable);
    UserDto addRoleToUser(Long userId, Long roleId);
    UserDto removeRoleFromUser(Long userId, Long roleId);
    List<String> getUserRoles(Long userId);
}

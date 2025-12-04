package com.enterprise.portfolio.service.impl;

import com.enterprise.portfolio.dto.UserDto;
import com.enterprise.portfolio.exception.ResourceAlreadyExistsException;
import com.enterprise.portfolio.exception.ResourceNotFoundException;
import com.enterprise.portfolio.model.Role;
import com.enterprise.portfolio.model.User;
import com.enterprise.portfolio.repository.RoleRepository;
import com.enterprise.portfolio.repository.UserRepository;
import com.enterprise.portfolio.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<UserDto, User, Long, UserRepository> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository repository, 
                          ModelMapper modelMapper,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository) {
        super(repository, User.class, UserDto.class);
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto findByUsername(String username) {
        return repository.findByUsername(username)
                .map(user -> super.toDto(user))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    
    @Override
    public User findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDto findByEmail(String email) {
        return repository.findByEmail(email)
                .map(user -> super.toDto(user))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public UserDto registerUser(UserDto userDto, String password) {
        if (existsByUsername(userDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }
        if (existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }

        User user = toEntity(userDto);
        user.setPassword(passwordEncoder.encode(password));
        
        // Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        user.setRoles(Collections.singleton(userRole));
        
        User savedUser = repository.save(user);
        return toDto(savedUser);
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    @Override
    public void requestPasswordReset(String email) {
        // Implementation for password reset request
        // This would typically generate a token and send an email
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Implementation for password reset
        // This would validate the token and update the password
    }

    @Override
    public void verifyEmail(String token) {
        // Implementation for email verification
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user");
        }
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return findByUsername(username);
    }

    @Override
    public Page<UserDto> searchUsers(String query, Pageable pageable) {
        return repository.searchUsers(query, pageable)
                .map(user -> super.toDto(user));
    }

    @Override
    public UserDto addRoleToUser(Long userId, Long roleId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        user.getRoles().add(role);
        User updatedUser = repository.save(user);
        return toDto(updatedUser);
    }

    @Override
    public UserDto removeRoleFromUser(Long userId, Long roleId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        user.getRoles().remove(role);
        User updatedUser = repository.save(user);
        return toDto(updatedUser);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}

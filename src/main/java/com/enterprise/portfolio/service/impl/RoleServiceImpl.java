package com.enterprise.portfolio.service.impl;

import com.enterprise.portfolio.dto.RoleDto;
import com.enterprise.portfolio.exception.ResourceAlreadyExistsException;
import com.enterprise.portfolio.exception.ResourceNotFoundException;
import com.enterprise.portfolio.model.Role;
import com.enterprise.portfolio.repository.RoleRepository;
import com.enterprise.portfolio.repository.UserRepository;
import com.enterprise.portfolio.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl extends BaseServiceImpl<RoleDto, Role, Long, RoleRepository> implements RoleService {

    private final UserRepository userRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository repository, UserRepository userRepository) {
        super(repository, Role.class, RoleDto.class);
        this.userRepository = userRepository;
    }

    @Override
    public RoleDto findByName(String name) {
        return repository.findByName(name)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public RoleDto create(RoleDto roleDto) {
        if (existsByName(roleDto.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDto.getName());
        }
        return super.create(roleDto);
    }

    @Override
    public RoleDto update(Long id, RoleDto roleDto) {
        Role existingRole = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Check if the name is being changed and if the new name already exists
        if (!existingRole.getName().equals(roleDto.getName()) && 
            repository.existsByName(roleDto.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDto.getName());
        }

        return super.update(id, roleDto);
    }

    @Override
    public List<RoleDto> findRolesByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return repository.findByUsers_Id(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

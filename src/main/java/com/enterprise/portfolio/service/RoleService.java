package com.enterprise.portfolio.service;

import com.enterprise.portfolio.dto.RoleDto;
import com.enterprise.portfolio.model.Role;

import java.util.List;

public interface RoleService extends BaseService<RoleDto, Role, Long> {
    RoleDto findByName(String name);
    boolean existsByName(String name);
    List<RoleDto> findRolesByUser(Long userId);
}

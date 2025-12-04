package com.enterprise.portfolio.controller;

import com.enterprise.portfolio.dto.RoleDto;
import com.enterprise.portfolio.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management API")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles with pagination")
    public ResponseEntity<Page<RoleDto>> getAllRoles(Pageable pageable) {
        return ResponseEntity.ok(roleService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get role by name")
    public ResponseEntity<RoleDto> getRoleByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.findByName(name));
    }

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        RoleDto createdRole = roleService.create(roleDto);
        return ResponseEntity
                .created(URI.create("/api/roles/" + createdRole.getId()))
                .body(createdRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing role")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.update(id, roleDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all roles for a specific user")
    public ResponseEntity<List<RoleDto>> getRolesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(roleService.findRolesByUser(userId));
    }
}

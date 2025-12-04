package com.enterprise.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto extends BaseDto {
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must be less than 50 characters")
    private String name;
    
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    
    private Set<String> permissions;
}

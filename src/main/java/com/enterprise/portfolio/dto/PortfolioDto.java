package com.enterprise.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
    private Long id;
    
    @NotBlank(message = "Portfolio name is required")
    private String name;
    
    private String description;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For creating a portfolio
    public static PortfolioDto fromCreateRequest(String name, String description, Long userId) {
        return PortfolioDto.builder()
                .name(name)
                .description(description)
                .userId(userId)
                .build();
    }
}

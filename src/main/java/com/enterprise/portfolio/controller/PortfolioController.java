package com.enterprise.portfolio.controller;

import com.enterprise.portfolio.dto.PortfolioDto;
import com.enterprise.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> getUserPortfolios(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(portfolioService.getUserPortfolios(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDto> getPortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(portfolioService.getPortfolio(id, userId));
    }

    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(
            @Valid @RequestBody PortfolioDto portfolioDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        PortfolioDto createdPortfolio = portfolioService.createPortfolio(portfolioDto, userId);
        return new ResponseEntity<>(createdPortfolio, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDto> updatePortfolio(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioDto portfolioDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, portfolioDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        portfolioService.deletePortfolio(id, userId);
        return ResponseEntity.noContent().build();
    }
}

package com.enterprise.portfolio.service;

import com.enterprise.portfolio.dto.PortfolioDto;
import com.enterprise.portfolio.dto.UserDto;
import com.enterprise.portfolio.exception.ResourceAlreadyExistsException;
import com.enterprise.portfolio.exception.ResourceNotFoundException;
import com.enterprise.portfolio.model.Portfolio;
import com.enterprise.portfolio.model.User;
import com.enterprise.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    
    private final PortfolioRepository portfolioRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    
    @Transactional(readOnly = true)
    public List<PortfolioDto> getUserPortfolios(Long userId) {
        User user = userService.findEntityById(userId);
        return portfolioRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PortfolioDto getPortfolio(Long portfolioId, Long userId) {
        User user = userService.findEntityById(userId);
        return portfolioRepository.findByIdAndUser(portfolioId, user)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId.toString()));
    }
    
    @Transactional
    public PortfolioDto createPortfolio(PortfolioDto portfolioDto, Long userId) {
        // Get the user entity directly from the service
        User user = userService.findEntityById(userId);
        
        if (portfolioRepository.existsByNameAndUser(portfolioDto.getName(), user)) {
            throw new ResourceAlreadyExistsException("Portfolio", "name", portfolioDto.getName());
        }
        
        Portfolio portfolio = new Portfolio();
        portfolio.setName(portfolioDto.getName());
        portfolio.setDescription(portfolioDto.getDescription());
        portfolio.setUser(user);
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return convertToDto(savedPortfolio);
    }
    
    @Transactional
    public PortfolioDto updatePortfolio(Long portfolioId, PortfolioDto portfolioDto, Long userId) {
        User user = userService.findEntityById(userId);
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId.toString()));
        
        // Check if the new name is already taken by another portfolio of the same user
        if (!portfolio.getName().equals(portfolioDto.getName()) && 
            portfolioRepository.existsByNameAndUser(portfolioDto.getName(), user)) {
            throw new ResourceAlreadyExistsException("Portfolio", "name", portfolioDto.getName());
        }
        
        portfolio.setName(portfolioDto.getName());
        portfolio.setDescription(portfolioDto.getDescription());
        
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return convertToDto(updatedPortfolio);
    }
    
    @Transactional
    public void deletePortfolio(Long portfolioId, Long userId) {
        User user = userService.findEntityById(userId);
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "id", portfolioId.toString()));
        
        portfolioRepository.delete(portfolio);
    }
    
    private PortfolioDto convertToDto(Portfolio portfolio) {
        PortfolioDto dto = modelMapper.map(portfolio, PortfolioDto.class);
        dto.setUserId(portfolio.getUser().getId());
        return dto;
    }
}

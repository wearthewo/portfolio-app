package com.enterprise.portfolio.repository;

import com.enterprise.portfolio.model.Portfolio;
import com.enterprise.portfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    List<Portfolio> findByUser(User user);
    
    Optional<Portfolio> findByIdAndUser(Long id, User user);
    
    boolean existsByNameAndUser(String name, User user);
    
    Optional<Portfolio> findByNameAndUser(String name, User user);
    
    long countByUser(User user);
}

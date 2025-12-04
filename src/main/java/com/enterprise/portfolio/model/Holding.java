package com.enterprise.portfolio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;
    
    @Column(name = "average_purchase_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal averagePurchasePrice;
    
    @Column(name = "total_investment", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalInvestment;
    
    @Column(name = "current_value", precision = 19, scale = 2)
    private BigDecimal currentValue;
    
    @Column(name = "profit_loss", precision = 19, scale = 2)
    private BigDecimal profitLoss;
    
    @Column(name = "profit_loss_percentage", precision = 10, scale = 2)
    private Double profitLossPercentage;
    
    // Helper methods for bidirectional relationship
    public void setPortfolio(Portfolio portfolio) {
        if (this.portfolio != null) {
            this.portfolio.getHoldings().remove(this);
        }
        this.portfolio = portfolio;
        if (portfolio != null) {
            portfolio.getHoldings().add(this);
        }
    }
}

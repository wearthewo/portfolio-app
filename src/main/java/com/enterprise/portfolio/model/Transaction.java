package com.enterprise.portfolio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {
    
    public enum TransactionType {
        BUY, SELL, DIVIDEND, DEPOSIT, WITHDRAWAL, FEE, REBALANCE
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = true) // Can be null for non-asset transactions
    private Asset asset;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;
    
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;
    
    @Column(name = "price_per_unit", precision = 19, scale = 8)
    private BigDecimal pricePerUnit;
    
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "transaction_fee", precision = 19, scale = 2)
    private BigDecimal transactionFee;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    private String notes;
    
    // Helper methods for bidirectional relationship
    public void setPortfolio(Portfolio portfolio) {
        if (this.portfolio != null) {
            this.portfolio.getTransactions().remove(this);
        }
        this.portfolio = portfolio;
        if (portfolio != null) {
            portfolio.getTransactions().add(this);
        }
    }
}

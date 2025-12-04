package com.enterprise.portfolio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset extends BaseEntity {
    
    public enum AssetType {
        CRYPTOCURRENCY, STOCK, ETF, FOREX, COMMODITY, OTHER
    }
    
    @NotBlank
    @Column(nullable = false, unique = true)
    private String symbol;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType type;
    
    @Column(length = 10)
    private String currency;
    
    @Column(precision = 19, scale = 8)
    private BigDecimal currentPrice;
    
    @Column(name = "price_updated_at")
    private java.time.LocalDateTime priceUpdatedAt;
    
    @Column(length = 10)
    private String exchange;
    
    @Column(name = "is_active")
    private boolean active = true;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return symbol.equals(asset.symbol);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
    
    @Override
    public String toString() {
        return "Asset{" +
                "id=" + getId() +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

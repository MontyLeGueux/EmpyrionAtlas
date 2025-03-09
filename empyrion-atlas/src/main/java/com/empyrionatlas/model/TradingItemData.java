package com.empyrionatlas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class TradingItemData {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "trader_id")
    private TraderData trader;
	
	private String itemName;
    private double minPrice;
    private double maxPrice;
    private int minQuantity;
    private int maxQuantity;
	
	public TradingItemData(String itemName, double minPrice, double maxPrice, int minQuantity, int maxQuantity, TraderData trader) {
        this.itemName = itemName;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.trader = trader;
    }
	
}

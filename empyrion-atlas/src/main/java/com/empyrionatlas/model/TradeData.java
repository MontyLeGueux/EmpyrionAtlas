package com.empyrionatlas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trades")
public class TradeData {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id", nullable = false)
    private TraderData trader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemData item;

    // Buying and Selling Quantity Ranges
    private int sellStockMin;
    private int sellStockMax;
    private int totalStockMin;
    private int totalStockMax;

    // Market Fluctuation Multipliers
    private double sellMarketFluctuationMin;
    private double sellMarketFluctuationMax;
    private double buyMarketFluctuationMin;
    private double buyMarketFluctuationMax;

    public TradeData() {}

    public TradeData(TraderData trader, ItemData item,
                     int sellStockMin, int sellStockMax, double sellMarketFluctuationMin, double sellMarketFluctuationMax,
                     int totalStockMin, int totalStockMax, double buyMarketFluctuationMin, double buyMarketFluctuationMax) {
        this.trader = trader;
        this.item = item;
        this.sellStockMin = sellStockMin;
        this.sellStockMax = sellStockMax;
        this.sellMarketFluctuationMin = sellMarketFluctuationMin;
        this.sellMarketFluctuationMax = sellMarketFluctuationMax;
        this.totalStockMin = totalStockMin;
        this.totalStockMax = totalStockMax;
        this.buyMarketFluctuationMin = buyMarketFluctuationMin;
        this.buyMarketFluctuationMax = buyMarketFluctuationMax;
    }
    
    public double getSellMarketFluctuationMin() { return sellMarketFluctuationMin; }
    public double getSellMarketFluctuationMax() { return sellMarketFluctuationMax; }
    public double getBuyMarketFluctuationMin() { return buyMarketFluctuationMin; }
    public double getBuyMarketFluctuationMax() { return buyMarketFluctuationMax; }
    
    public int getSellStockMin() { return sellStockMin; }
    public int getSellStockMax() { return sellStockMax; }
    public int getTotalStockMin() { return totalStockMin; }
    public int getTotalStockMax() { return totalStockMax; }
    
    public TraderData getTrader() { return trader; }

    public double getAverageSellPrice() {
        return item.getBasePrice() * ((sellMarketFluctuationMin + sellMarketFluctuationMax) / 2);
    }

    public double getAverageBuyPrice() {
        return item.getBasePrice() * ((buyMarketFluctuationMin + buyMarketFluctuationMax) / 2);
    }
    
    public int getAverageSellVolume() {
        return (int) ((sellStockMin + sellStockMax) / 2);
    }

    public int getAverageBuyVolume() {
        return (int) (((totalStockMin + totalStockMax) / 2) - getAverageSellVolume());
    }
}

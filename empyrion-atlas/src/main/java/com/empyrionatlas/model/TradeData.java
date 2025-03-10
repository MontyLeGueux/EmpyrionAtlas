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

    public double getAdjustedSellPrice() {
        return item.getBasePrice() * ((sellMarketFluctuationMin + sellMarketFluctuationMax) / 2);
    }

    public double getAdjustedBuyPrice() {
        return item.getBasePrice() * ((buyMarketFluctuationMin + buyMarketFluctuationMax) / 2);
    }
}

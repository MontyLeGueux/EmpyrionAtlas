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
    
    private int fixedSellPriceMin;
    private int fixedSellPriceMax;
    private int fixedBuyPriceMin;
    private int fixedBuyPriceMax;

    public TradeData() {}

    public TradeData(TraderData trader, ItemData item, int sellStockMin, int sellStockMax, int totalStockMin,
			int totalStockMax, double sellMarketFluctuationMin, double sellMarketFluctuationMax,
			double buyMarketFluctuationMin, double buyMarketFluctuationMax, int fixedSellPriceMin,
			int fixedSellPriceMax, int fixedBuyPriceMin, int fixedBuyPriceMax) {
		this.trader = trader;
		this.item = item;
		this.sellStockMin = sellStockMin;
		this.sellStockMax = sellStockMax;
		this.totalStockMin = totalStockMin;
		this.totalStockMax = totalStockMax;
		this.sellMarketFluctuationMin = sellMarketFluctuationMin;
		this.sellMarketFluctuationMax = sellMarketFluctuationMax;
		this.buyMarketFluctuationMin = buyMarketFluctuationMin;
		this.buyMarketFluctuationMax = buyMarketFluctuationMax;
		this.fixedSellPriceMin = fixedSellPriceMin;
		this.fixedSellPriceMax = fixedSellPriceMax;
		this.fixedBuyPriceMin = fixedBuyPriceMin;
		this.fixedBuyPriceMax = fixedBuyPriceMax;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TraderData getTrader() {
		return trader;
	}

	public void setTrader(TraderData trader) {
		this.trader = trader;
	}

	public ItemData getItem() {
		return item;
	}

	public void setItem(ItemData item) {
		this.item = item;
	}

	public int getSellStockMin() {
		return sellStockMin;
	}

	public void setSellStockMin(int sellStockMin) {
		this.sellStockMin = sellStockMin;
	}

	public int getSellStockMax() {
		return sellStockMax;
	}

	public void setSellStockMax(int sellStockMax) {
		this.sellStockMax = sellStockMax;
	}

	public int getTotalStockMin() {
		return totalStockMin;
	}

	public void setTotalStockMin(int totalStockMin) {
		this.totalStockMin = totalStockMin;
	}

	public int getTotalStockMax() {
		return totalStockMax;
	}

	public void setTotalStockMax(int totalStockMax) {
		this.totalStockMax = totalStockMax;
	}

	public double getSellMarketFluctuationMin() {
		return sellMarketFluctuationMin;
	}

	public void setSellMarketFluctuationMin(double sellMarketFluctuationMin) {
		this.sellMarketFluctuationMin = sellMarketFluctuationMin;
	}

	public double getSellMarketFluctuationMax() {
		return sellMarketFluctuationMax;
	}

	public void setSellMarketFluctuationMax(double sellMarketFluctuationMax) {
		this.sellMarketFluctuationMax = sellMarketFluctuationMax;
	}

	public double getBuyMarketFluctuationMin() {
		return buyMarketFluctuationMin;
	}

	public void setBuyMarketFluctuationMin(double buyMarketFluctuationMin) {
		this.buyMarketFluctuationMin = buyMarketFluctuationMin;
	}

	public double getBuyMarketFluctuationMax() {
		return buyMarketFluctuationMax;
	}

	public void setBuyMarketFluctuationMax(double buyMarketFluctuationMax) {
		this.buyMarketFluctuationMax = buyMarketFluctuationMax;
	}

	public int getFixedSellPriceMin() {
		return fixedSellPriceMin;
	}

	public void setFixedSellPriceMin(int fixedSellPriceMin) {
		this.fixedSellPriceMin = fixedSellPriceMin;
	}

	public int getFixedSellPriceMax() {
		return fixedSellPriceMax;
	}

	public void setFixedSellPriceMax(int fixedSellPriceMax) {
		this.fixedSellPriceMax = fixedSellPriceMax;
	}

	public int getFixedBuyPriceMin() {
		return fixedBuyPriceMin;
	}

	public void setFixedBuyPriceMin(int fixedBuyPriceMin) {
		this.fixedBuyPriceMin = fixedBuyPriceMin;
	}

	public int getFixedBuyPriceMax() {
		return fixedBuyPriceMax;
	}

	public void setFixedBuyPriceMax(int fixedBuyPriceMax) {
		this.fixedBuyPriceMax = fixedBuyPriceMax;
	}

	public double getAverageSellPrice() {
    	return (fixedSellPriceMin == 0 && fixedSellPriceMax == 0 ? item.getBasePrice() * ((sellMarketFluctuationMin + sellMarketFluctuationMax) / 2) : ((fixedSellPriceMin + fixedSellPriceMax) / 2));
    }

    public double getAverageBuyPrice() {
        return (fixedBuyPriceMin == 0 && fixedBuyPriceMax == 0 ? item.getBasePrice() * ((buyMarketFluctuationMin + buyMarketFluctuationMax) / 2) : ((fixedBuyPriceMin + fixedBuyPriceMax) / 2));
    }
    
    public int getAverageSellVolume() {
        return (int) ((sellStockMin + sellStockMax) / 2);
    }

    public int getAverageBuyVolume() {
        return (int) (((totalStockMin + totalStockMax) / 2) - getAverageSellVolume());
    }
}

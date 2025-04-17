package com.empyrionatlas.dto.parsing;

public class ParsedTradeDTO {

	private String traderStringID;
    private String itemStringID;

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
    
	public ParsedTradeDTO(String traderStringID, String itemStringID, int sellStockMin, int sellStockMax,
			int totalStockMin, int totalStockMax, double sellMarketFluctuationMin, double sellMarketFluctuationMax,
			double buyMarketFluctuationMin, double buyMarketFluctuationMax, int fixedSellPriceMin,
			int fixedSellPriceMax, int fixedBuyPriceMin, int fixedBuyPriceMax) {
		super();
		this.traderStringID = traderStringID;
		this.itemStringID = itemStringID;
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

	public String getTraderStringID() {
		return traderStringID;
	}

	public String getItemStringID() {
		return itemStringID;
	}

	public int getSellStockMin() {
		return sellStockMin;
	}

	public int getSellStockMax() {
		return sellStockMax;
	}

	public int getTotalStockMin() {
		return totalStockMin;
	}

	public int getTotalStockMax() {
		return totalStockMax;
	}

	public double getSellMarketFluctuationMin() {
		return sellMarketFluctuationMin;
	}

	public double getSellMarketFluctuationMax() {
		return sellMarketFluctuationMax;
	}

	public double getBuyMarketFluctuationMin() {
		return buyMarketFluctuationMin;
	}

	public double getBuyMarketFluctuationMax() {
		return buyMarketFluctuationMax;
	}

	public int getFixedSellPriceMin() {
		return fixedSellPriceMin;
	}

	public int getFixedSellPriceMax() {
		return fixedSellPriceMax;
	}

	public int getFixedBuyPriceMin() {
		return fixedBuyPriceMin;
	}

	public int getFixedBuyPriceMax() {
		return fixedBuyPriceMax;
	}
    
    
}

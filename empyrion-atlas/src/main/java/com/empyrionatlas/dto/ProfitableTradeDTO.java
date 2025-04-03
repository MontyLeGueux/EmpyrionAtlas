package com.empyrionatlas.dto;

import java.util.List;

public class ProfitableTradeDTO {

	private String itemName;
    private List<String> buyStation;
    private List<String> sellStation;
    private String buyTrader;
    private String sellTrader;
    private double buyPrice;
    private double sellPrice;
    private int amount;
    private double totalProfit;
    
	public ProfitableTradeDTO(String itemName, List<String> buyStation, List<String> sellStation, String buyTrader, String sellTrader,  double buyPrice, double sellPrice,
			int amount, double totalProfit) {
		super();
		this.itemName = itemName;
		this.buyStation = buyStation;
		this.sellStation = sellStation;
		this.buyTrader = buyTrader;
		this.sellTrader = sellTrader;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.amount = amount;
		this.totalProfit = totalProfit;
	}

	public String getItemName() {
		return itemName;
	}

	public List<String> getBuyStations() {
		return buyStation;
	}

	public List<String> getSellStations() {
		return sellStation;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public int getAmount() {
		return amount;
	}

	public double getTotalProfit() {
		return totalProfit;
	}

	public String getBuyTrader() {
		return buyTrader;
	}

	public String getSellTrader() {
		return sellTrader;
	}

}

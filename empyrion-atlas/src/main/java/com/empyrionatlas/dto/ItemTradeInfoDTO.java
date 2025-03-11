package com.empyrionatlas.dto;

public class ItemTradeInfoDTO {
    private String itemName;
    private double avgSellPrice;
    private double avgBuyPrice;
    private int avgSellVolume;
    private int avgBuyVolume;
    private TraderDTO trader;
    
    public ItemTradeInfoDTO(String itemName, double avgSellPrice, double avgBuyPrice, int avgSellVolume, int avgBuyVolume, TraderDTO trader) {
        this.itemName = itemName;
        this.avgSellPrice = avgSellPrice;
        this.avgBuyPrice = avgBuyPrice;
        this.avgSellVolume = avgSellVolume;
        this.avgBuyVolume = avgBuyVolume;
        this.trader = trader;
    }

    public String getItemName() { return itemName; }
    public double getAvgSellPrice() { return avgSellPrice; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public int getAvgSellVolume() { return avgSellVolume; }
    public int getAvgBuyVolume() { return avgBuyVolume; }
    public TraderDTO getTrader() { return trader; }
}
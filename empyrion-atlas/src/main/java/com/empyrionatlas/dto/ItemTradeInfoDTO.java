package com.empyrionatlas.dto;

import java.util.List;

public class ItemTradeInfoDTO {
    private String itemName;
    private double avgSellPrice;
    private double avgBuyPrice;
    private int avgSellVolume;
    private int avgBuyVolume;
    private List<TraderInstanceDTO> traders;
    
    public ItemTradeInfoDTO(String itemName, double avgSellPrice, double avgBuyPrice, int avgSellVolume, int avgBuyVolume, List<TraderInstanceDTO> traders) {
        this.itemName = itemName;
        this.avgSellPrice = avgSellPrice;
        this.avgBuyPrice = avgBuyPrice;
        this.avgSellVolume = avgSellVolume;
        this.avgBuyVolume = avgBuyVolume;
        this.traders = traders;
    }

    public String getItemName() { return itemName; }
    public double getAvgSellPrice() { return avgSellPrice; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public int getAvgSellVolume() { return avgSellVolume; }
    public int getAvgBuyVolume() { return avgBuyVolume; }
    public List<TraderInstanceDTO> getTraders() { return traders; }
}
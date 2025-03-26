package com.empyrionatlas.dto;

public class TraderInstanceDTO {

	private String traderName;
	
	private int restockTimer;
	
	public TraderInstanceDTO(String traderName, int restockTimer) {
        this.traderName = traderName;
        this.restockTimer = restockTimer;
    }

    public String getTraderName() { return traderName; }
    public int getRestockTimer() { return restockTimer; }
}

package com.empyrionatlas.dto;

public class TraderInstanceDTO {

	private String traderName;
	
	private int restockTimer;
	
	private String stationName;
	
	public TraderInstanceDTO(String traderName, int restockTimer, String stationName) {
        this.traderName = traderName;
        this.restockTimer = restockTimer;
        this.stationName = stationName;
    }

    public String getTraderName() { return traderName; }
    public int getRestockTimer() { return restockTimer; }
	public String getStationName() { return stationName; }
}

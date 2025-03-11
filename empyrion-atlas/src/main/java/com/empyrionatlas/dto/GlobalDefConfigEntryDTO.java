package com.empyrionatlas.dto;

public class GlobalDefConfigEntryDTO {
	private String name;
	private int marketPrice;
	
	public GlobalDefConfigEntryDTO(String name, int marketPrice) {
		this.name = name;
		this.marketPrice = marketPrice;
	}
	
	public String getName() { return name; }
	public int getMarketPrice() { return marketPrice; }
}
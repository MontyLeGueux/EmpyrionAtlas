package com.empyrionatlas.dto.parsing;

public class ParsedItemDTO {
	
	private String stringID;
	private String globalRef;
	
	private double basePrice;

	public ParsedItemDTO(String stringID, String globalRef, double basePrice) {
		super();
		this.stringID = stringID;
		this.globalRef = globalRef;
		this.basePrice = basePrice;
	}

	public String getStringID() {
		return stringID;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public String getGlobalRef() {
		return globalRef;
	}
}
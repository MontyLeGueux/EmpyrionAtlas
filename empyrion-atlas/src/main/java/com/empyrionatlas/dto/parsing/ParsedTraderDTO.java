package com.empyrionatlas.dto.parsing;

import java.util.List;

public class ParsedTraderDTO {
	
	private String stringID;
	
	private List<ParsedTradeDTO> trades;
	
	public ParsedTraderDTO(String stringID, List<ParsedTradeDTO> trades) {
		super();
		this.stringID = stringID;
		this.trades = trades;
	}

	public String getStringID() {
		return stringID;
	}

	public List<ParsedTradeDTO> getTrades() {
		return trades;
	}
}

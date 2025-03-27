package com.empyrionatlas.dto;

import java.util.List;

public class ItemTradeSearchResultDTO {

	private boolean itemExists;
	private List<ItemTradeInfoDTO> trades;
	
	public ItemTradeSearchResultDTO(boolean itemExists, List<ItemTradeInfoDTO> trades) {
        this.itemExists = itemExists;
        this.trades = trades;
    }
	
	public boolean isItemExists() { return itemExists; }
	public List<ItemTradeInfoDTO> getTrades() { return trades;}
}

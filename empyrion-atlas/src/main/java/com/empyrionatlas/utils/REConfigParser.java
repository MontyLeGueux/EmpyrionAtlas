package com.empyrionatlas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.TradeData;
import com.empyrionatlas.service.ModTradingDataService;

public class REConfigParser {
	
	private static final Logger logger = LoggerFactory.getLogger(REConfigParser.class);
	
	private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("Name:\\s*(\\S+)");
    private static final Pattern ITEM_PRICE_PATTERN = Pattern.compile("MarketPrice:\\s*(\\d+(\\.\\d+)?)");
	
	public static List<ItemData> parseItemConfigFile(File ecfFile) throws IOException {
		List<ItemData> items = new ArrayList<>();
		String itemName = null;
        double marketPrice = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(ecfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                Matcher nameMatcher = ITEM_NAME_PATTERN.matcher(line);
                Matcher priceMatcher = ITEM_PRICE_PATTERN.matcher(line);

                if (nameMatcher.find()) {
                    itemName = nameMatcher.group(1);
                } else if (priceMatcher.find()) {
                    marketPrice = Double.parseDouble(priceMatcher.group(1));
                }

                if (itemName != null && marketPrice > 0) {
                    items.add(new ItemData(itemName, marketPrice));
                    itemName = null;
                    marketPrice = 0;
                }
            }
        }
		return items;
	}

	public static TradeConfigParseResultDTO parseTraderConfigFile(File ecfFile, Map<String, ItemData> itemCache) throws IOException {
        List<TraderData> traders = new ArrayList<>();
        TraderData currentTrader = null;
        List<TradeData> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ecfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Ignore comments and empty lines
                }

                if (line.startsWith("{")) {
                    if (currentTrader != null) {
                        currentTrader.setItemsForSale(items);
                        traders.add(currentTrader); // Save the previous trader before starting a new one
                    }
                    currentTrader = new TraderData();
                    items = new ArrayList<>();
                    line = line.substring(1); //Removing the { so the rest of the code can read the line
                    line.trim();
                }

                if (line.startsWith("}")) {
                    if (currentTrader != null) {
                        currentTrader.setItemsForSale(items);
                        traders.add(currentTrader); // Add the last trader in the file
                    }
                    continue; // End of a trader block
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim().replaceAll("\"", ""); // Remove quotes
                if (currentTrader != null) {
                    switch (key) { //add extra trader values here as needed.
                        case "Trader Name" -> {
                        	currentTrader.setName(value);
                        }
                        default -> {
                            if (key.startsWith("Item")) {
                                items.add(parseTrade(value, currentTrader, itemCache));
                            }
                        }
                    }
                }
            }
        }
		return new TradeConfigParseResultDTO(traders);
	}
	
	private static TradeData parseTrade(String itemLine, TraderData trader, Map<String, ItemData> itemCache) {
		logger.info("Parsing item from " + itemLine);
        String[] parts = itemLine.split(",");
        String itemName = parts[0].trim();
        
        ItemData item = itemCache.get(itemName);
        if(item == null) {
        	logger.error("Couldn't find item : " + itemName + " in cache, aborting trade parsing");
        	return null;
        }

        double minSellMF = 0, maxSellMF = 0, minBuyMF = 0, maxBuyMF = 0;
        int sellStockMin = 0, sellStockMax = 0, totalStockMin = 0, totalStockMax = 0;
        boolean isParsingSell = true;

        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("mf=")) {
            	String[] range = part.replace("mf=", "").split("-");
            	if(isParsingSell) {
                    minSellMF = Double.parseDouble(range[0]);
                    maxSellMF = Double.parseDouble(range[1]);
            	}
            	else {
            		minBuyMF = Double.parseDouble(range[0]);
            		maxBuyMF = Double.parseDouble(range[1]);
            	}
            } else if (part.matches("\\d+-\\d+")) {
                String[] range = part.split("-");
                if(isParsingSell) {
                	sellStockMin = Integer.parseInt(range[0]);
                	sellStockMax = Integer.parseInt(range[1]);
                	isParsingSell = false; 
                	//if we're parsing the sell quantities then we're done with the sell market fluctuations too
                }
                else {
                	totalStockMin = Integer.parseInt(range[0]);
                	totalStockMax = Integer.parseInt(range[1]);
                }
            }
        }

        return new TradeData(trader, item, sellStockMin, sellStockMax, minSellMF, maxSellMF, totalStockMin, totalStockMax, minBuyMF, maxBuyMF);
    }
}

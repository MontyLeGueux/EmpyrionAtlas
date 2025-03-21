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

import com.empyrionatlas.dto.GlobalDefConfigEntryDTO;
import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.TradeData;

public class REConfigParser {
	
	private static final Logger logger = LoggerFactory.getLogger(REConfigParser.class);
	
	private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("Name:\\s*(\\S+)");
	private static final Pattern ITEM_GLOBALREF_PATTERN = Pattern.compile("GlobalRef:\\s*(\\S+)");
    private static final Pattern ITEM_PRICE_PATTERN = Pattern.compile("MarketPrice:\\s*(\\d+(\\.\\d+)?)");
	
	public static List<ItemData> parseItemConfigFile(File ecfFile, File globalDefFile) throws IOException {
		List<ItemData> items = new ArrayList<>();
		String itemName = null;
        int marketPrice = 0;
        List<GlobalDefConfigEntryDTO> globalDefConfig = new ArrayList<>();
        
        logger.info("Parsing global def config file ...");
        
        try (BufferedReader br = new BufferedReader(new FileReader(globalDefFile))) {
        	String line;
            while ((line = br.readLine()) != null) {
            	Matcher nameMatcher = ITEM_NAME_PATTERN.matcher(line);
                Matcher priceMatcher = ITEM_PRICE_PATTERN.matcher(line);
                
                if (nameMatcher.find()) {
                	itemName = nameMatcher.group(1);
                } else if (priceMatcher.find()) {
                    marketPrice = Integer.parseInt(priceMatcher.group(1));
                }
                
                if (itemName != null && marketPrice > 0) {
                	globalDefConfig.add(new GlobalDefConfigEntryDTO(itemName, (int)marketPrice));
                    itemName = null;
                    marketPrice = 0;
                }
            }
        }
        
        itemName = null;
        marketPrice = 0;
        
        logger.info("Parsing item config file ...");

        try (BufferedReader br = new BufferedReader(new FileReader(ecfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                Matcher nameMatcher = ITEM_NAME_PATTERN.matcher(line);
                Matcher priceMatcher = ITEM_PRICE_PATTERN.matcher(line);
                Matcher globalRefMatcher = ITEM_GLOBALREF_PATTERN.matcher(line);

                if (nameMatcher.find()) {
                    itemName = nameMatcher.group(1);
                    if(itemName.contains(",")) {
                    	itemName = itemName.substring(0, itemName.length() - 1);
                    }
                } else if (priceMatcher.find()) {
                    marketPrice = Integer.parseInt(priceMatcher.group(1));
                } else if(globalRefMatcher.find()) {
                	GlobalDefConfigEntryDTO result = globalDefConfig.stream()
                            .filter(dto -> dto.getName().equals(globalRefMatcher.group(1)))
                            .findFirst()
                            .orElse(null);
                	if(result != null) {
                		marketPrice = result.getMarketPrice();
                		logger.info("Found item " + itemName + " with globalRef " + globalRefMatcher.group(1));
                	}
                }

                if (itemName != null && marketPrice > 0) {
                    items.add(new ItemData(itemName, marketPrice));
                    itemName = null;
                    marketPrice = 0;
                }
            }
        }
        logger.info("Parsed " + items.size() + " items");
		return items;
	}

	public static TradeConfigParseResultDTO parseTraderConfigFile(File ecfFile, Map<String, ItemData> itemCache) throws IOException {
        List<TraderData> traders = new ArrayList<>();
        TraderData currentTrader = null;
        List<TradeData> items = new ArrayList<>();

        logger.info("Parsing traders config file ...");
        
        try (BufferedReader br = new BufferedReader(new FileReader(ecfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("{")) {
                    if (currentTrader != null) {
                        currentTrader.setItemsForSale(items);
                        traders.add(currentTrader);
                    }
                    currentTrader = new TraderData();
                    items = new ArrayList<>();
                    line = line.substring(1);
                    line.trim();
                }

                if (line.startsWith("}")) {
                    if (currentTrader != null) {
                        currentTrader.setItemsForSale(items);
                        traders.add(currentTrader);
                    }
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim().replaceAll("\"", "");
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
        logger.info("Parsed " + traders.size() + " traders");
		return new TradeConfigParseResultDTO(traders);
	}
	
	private static TradeData parseTrade(String itemLine, TraderData trader, Map<String, ItemData> itemCache) {
		logger.info("Parsing item from " + itemLine);
        String[] parts = itemLine.split(",");
        String itemName = parts[0].trim();
        String[] range;
        
        ItemData item = itemCache.get(itemName);
        if(item == null) {
        	logger.error("Couldn't find item : " + itemName + " in cache, aborting trade parsing");
        	return null;
        }

        double minSellMF = 0, maxSellMF = 0, minBuyMF = 0, maxBuyMF = 0;
        int sellStockMin = 0, sellStockMax = 0, totalStockMin = 0, totalStockMax = 0;
        boolean isParsingSell = true;

        //Parse sell data
        if(parts.length >= 3) {
        	range = parts[1].replace("mf=", "").split("-");
        	minSellMF = Double.parseDouble(range[0].trim());
        	if(range.length > 1) {
        		maxSellMF = Double.parseDouble(range[1].trim());
        	}
        	else {
        		maxSellMF = 0;
        	}
            logger.info("Parsing sell mf :" + minSellMF + " " + maxSellMF);
            
            parts[2] = parts[2].split("#")[0];
            range = parts[2].split("-");
            sellStockMin = Integer.parseInt(range[0].trim());
            if(range.length > 1) {
            	sellStockMax = Integer.parseInt(range[1].trim());
            }
            else {
            	sellStockMax = 0;
            }
            
        	logger.info("Parsing sell stock :" + sellStockMin + " " + sellStockMax);
        }
        //Parse buy data
        if(parts.length >= 5) {
        	range = parts[3].replace("mf=", "").split("-");
        	minBuyMF = Double.parseDouble(range[0].trim());
        	if(range.length > 1) {
        		maxBuyMF = Double.parseDouble(range[1].trim());
        	}
        	else {
        		maxBuyMF = 0;
        	}
    		
    		logger.info("Parsing buy mf :" + minBuyMF + " " + maxBuyMF);
        	
    		parts[4] = parts[4].split("#")[0];
    		range = parts[4].split("-");
    		totalStockMin = Integer.parseInt(range[0].trim());
    		if(range.length > 1) {
    			totalStockMax = Integer.parseInt(range[1].trim());
    		}
    		else {
    			totalStockMax = 0;
    		}
        	logger.info("Parsing total stock :" + totalStockMin + " " + totalStockMax);
        }
        /*
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("mf=")) {
            	String[] range = part.replace("mf=", "").split("-");
            	if(isParsingSell) {
                    minSellMF = Double.parseDouble(range[0]);
                    maxSellMF = Double.parseDouble(range[1]);
                    logger.info("Parsing sell mf :" + minSellMF + " " + maxSellMF);
            	}
            	else {
            		minBuyMF = Double.parseDouble(range[0]);
            		maxBuyMF = Double.parseDouble(range[1]);
            		logger.info("Parsing buy mf :" + minBuyMF + " " + maxBuyMF);
            	}
            } else if (part.matches("\\d+-\\d+")) {
                String[] range = part.split("-");
                if(isParsingSell) {
                	sellStockMin = Integer.parseInt(range[0]);
                	sellStockMax = Integer.parseInt(range[1]);
                	isParsingSell = false; 
                	logger.info("Parsing sell stock :" + sellStockMin + " " + sellStockMax);
                	//if we're parsing the sell quantities then we're done with the sell market fluctuations too
                }
                else {
                	totalStockMin = Integer.parseInt(range[0]);
                	totalStockMax = Integer.parseInt(range[1]);
                	logger.info("Parsing total stock :" + totalStockMin + " " + totalStockMax);
                }
            }
        }
        */

        return new TradeData(trader, item, sellStockMin, sellStockMax, minSellMF, maxSellMF, totalStockMin, totalStockMax, minBuyMF, maxBuyMF);
    }
}

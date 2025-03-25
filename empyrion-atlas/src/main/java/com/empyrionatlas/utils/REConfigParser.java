package com.empyrionatlas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.GlobalDefConfigEntryDTO;
import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.TradeData;
import com.empyrionatlas.model.TraderData;

public class REConfigParser {
	
	private static final Logger logger = LoggerFactory.getLogger(REConfigParser.class);
	
	private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("\\sName:\\s*(\\S+)");
	private static final Pattern ITEM_GLOBALREF_PATTERN = Pattern.compile("GlobalRef:\\s*(\\S+)");
    private static final Pattern ITEM_PRICE_PATTERN = Pattern.compile("MarketPrice:\\s*(\\d+(\\.\\d+)?)");
	
	public static List<ItemData> parseItemConfigFile(File itemsFile, File blocksFile, File globalDefFile, File localizationFile) throws IOException {
		List<ItemData> items = new ArrayList<>();

        List<GlobalDefConfigEntryDTO> globalDefConfig = parseGlobalDef(globalDefFile);

        Map<String, String> itemNames = parseItemNames(localizationFile);
        
        logger.info("Parsing item config file ...");
        items = parseItemFile(itemsFile, items, globalDefConfig, itemNames);
        
        logger.info("Parsing blocks config file ...");
        items = parseItemFile(blocksFile, items, globalDefConfig, itemNames);
        
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

        return new TradeData(trader, item, sellStockMin, sellStockMax, minSellMF, maxSellMF, totalStockMin, totalStockMax, minBuyMF, maxBuyMF);
    }
	
	private static List<GlobalDefConfigEntryDTO> parseGlobalDef(File globalDefFile) throws IOException {
		List<GlobalDefConfigEntryDTO> globalDefConfig = new ArrayList<>();
		String line;
		String itemStringID = null;
		int marketPrice = 0;
		
		logger.info("Parsing global def config file ...");
        
        try (BufferedReader br = new BufferedReader(new FileReader(globalDefFile))) {
        	
            while ((line = br.readLine()) != null) {
            	Matcher nameMatcher = ITEM_NAME_PATTERN.matcher(line);
                Matcher priceMatcher = ITEM_PRICE_PATTERN.matcher(line);
                
                if (nameMatcher.find()) {
                	itemStringID = nameMatcher.group(1);
                } else if (priceMatcher.find()) {
                    marketPrice = Integer.parseInt(priceMatcher.group(1));
                }
                
                if (itemStringID != null && marketPrice > 0) {
                	globalDefConfig.add(new GlobalDefConfigEntryDTO(itemStringID, (int)marketPrice));
                    itemStringID = null;
                    marketPrice = 0;
                }
            }
        }
        
		return globalDefConfig;
	}
	
	private static Map<String, String> parseItemNames(File localizationFile) throws IOException {
		Map<String, String> itemNames = new HashMap<String, String>();
		String line;
		
		 logger.info("Parsing localization config file ...");
	        
        try (BufferedReader br = new BufferedReader(new FileReader(localizationFile))) {
            while ((line = br.readLine()) != null) {
            	String[] splitResult = line.trim().split(",");
            	if(splitResult.length >= 2) {
            		//need to eliminate some formatting codes
            		itemNames.put(splitResult[0], splitResult[1].replaceAll("\\[(\\/)?[a-zA-Z0-9]+\\]|\\[-\\]|\"", ""));
            	}
            }
        }
        return itemNames;
	}
	
	private static List<ItemData> parseItemFile(File itemsFile, List<ItemData> items, List<GlobalDefConfigEntryDTO> globalDefConfig, Map<String, String> itemNames) throws FileNotFoundException, IOException {
		String itemStringID = null;
        int marketPrice = 0;
        String line;
		
		try (BufferedReader br = new BufferedReader(new FileReader(itemsFile))) {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                Matcher nameMatcher = ITEM_NAME_PATTERN.matcher(line);
                Matcher priceMatcher = ITEM_PRICE_PATTERN.matcher(line);
                Matcher globalRefMatcher = ITEM_GLOBALREF_PATTERN.matcher(line);

                if (nameMatcher.find()) {
                    itemStringID = nameMatcher.group(1);
                    logger.info("Found item with stringID : " + itemStringID);
                    if(itemStringID.contains(",")) {
                    	itemStringID = itemStringID.substring(0, itemStringID.length() - 1);
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
                		logger.info("Found item " + itemStringID + " with globalRef " + globalRefMatcher.group(1));
                	}
                }

                if (itemStringID != null && marketPrice > 0) {
                	if(itemNames.containsKey(itemStringID)) {
                		items.add(new ItemData(itemStringID, marketPrice, itemNames.get(itemStringID)));
                		logger.info("Item : " + itemStringID + " with name " + itemNames.get(itemStringID) + " successfully added");
                	}
                	else {
                		logger.info("Couldn't find name for item : " + itemStringID + " in localization list");
                	}
                    itemStringID = null;
                    marketPrice = 0;
                }
            }
        }
		return items;
	}
}

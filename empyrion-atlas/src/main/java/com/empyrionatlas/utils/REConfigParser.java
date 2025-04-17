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
import com.empyrionatlas.dto.parsing.ParsedItemDTO;
import com.empyrionatlas.dto.parsing.ParsedTradeDTO;
import com.empyrionatlas.dto.parsing.ParsedTraderDTO;

public class REConfigParser {
	
	private static final Logger logger = LoggerFactory.getLogger(REConfigParser.class);
	
	private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("\\sName:\\s*(\\S+)");
	private static final Pattern ITEM_GLOBALREF_PATTERN = Pattern.compile("GlobalRef:\\s*(\\S+)");
    private static final Pattern ITEM_PRICE_PATTERN = Pattern.compile("MarketPrice:\\s*(\\d+(\\.\\d+)?)");
	
	public static Map<String, ParsedItemDTO> parseItemsAndBlocksConfigFile(File itemsFile, File blocksFile) throws IOException {
		Map<String, ParsedItemDTO> items;
        
        logger.info("Parsing item config file ...");
        items = parseItemFile(itemsFile);
        
        logger.info("Parsing blocks config file ...");
        items.putAll(parseItemFile(blocksFile));
        
        logger.info("Parsed " + items.size() + " items");
		return items;
	}

	public static Map<String, ParsedTraderDTO> parseTraderConfigFile(File ecfFile) throws IOException {
		Map<String, ParsedTraderDTO> result = new HashMap<String, ParsedTraderDTO>();
        String currentTraderID = null;
        List<ParsedTradeDTO> currentTraderTrades = new ArrayList<ParsedTradeDTO>();
        
        logger.info("Parsing traders config file ...");
        
        try (BufferedReader br = new BufferedReader(new FileReader(ecfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("{")) {
                    line = line.substring(1);
                    line.trim();
                }

                if (line.startsWith("}")) {
                	if (currentTraderID != null) {
                    	logger.info("Finished parsing trader : " + currentTraderID + " with " + currentTraderTrades.size() + " trades");
                    	result.put(currentTraderID, new ParsedTraderDTO(currentTraderID, new ArrayList<>(currentTraderTrades)));
                    	currentTraderID = null;
                    	currentTraderTrades.clear();
                    }
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim().replaceAll("\"", "");
                switch (key) { //add extra trader values here as needed.
                    case "Trader Name" -> {
                    	currentTraderID = value;
                    }
                    default -> {
                        if (key.startsWith("Item")) {
                        	logger.info("Parsing trade with line : " + value);
                        	currentTraderTrades.add(parseTrade(value, currentTraderID));
                        }
                    }
                }
            }
        }
        logger.info("Parsed " + result.size() + " traders");
		return result;
	}
	
	private static ParsedTradeDTO parseTrade(String itemLine, String traderName) {
		logger.info("Parsing item from " + itemLine);
        String[] parts = itemLine.split(",");
        String itemName = parts[0].trim();
        String[] range;
        
        double minSellMF = 0, maxSellMF = 0, minBuyMF = 0, maxBuyMF = 0;
        
        int fixedSellPriceMin = 0, fixedBuyPriceMin = 0, fixedBuyPriceMax = 0, fixedSellPriceMax = 0;
        int sellStockMin = 0, sellStockMax = 0, totalStockMin = 0, totalStockMax = 0;

        //Parse sell data
        if(parts.length >= 3) {
        	//check if the price is fixed or expressed with market fluctuations
        	if(parts[1].contains("mf=")) {
        		range = parts[1].replace("mf=", "").split("-");
            	minSellMF = Double.parseDouble(range[0].trim());
            	if(range.length > 1) {
            		maxSellMF = Double.parseDouble(range[1].trim());
            	}
            	else {
            		maxSellMF = minSellMF;
            	}
            	logger.info("Parsing sell mf :" + minSellMF + " " + maxSellMF);
        	}
        	else {
        		range = parts[1].split("-");
        		fixedSellPriceMin = Integer.parseInt(range[0].trim());
        		if(range.length > 1) {
        			fixedSellPriceMax = Integer.parseInt(range[1].trim());
        		}
        		else {
        			fixedSellPriceMax = fixedSellPriceMin;
        		}
        		logger.info("Parsing sell fixed price :" + fixedSellPriceMin + " " + fixedSellPriceMin);
        	}

            parts[2] = parts[2].split("#")[0];
            range = parts[2].split("-");
            sellStockMin = Integer.parseInt(range[0].trim());
            if(range.length > 1) {
            	sellStockMax = Integer.parseInt(range[1].trim());
            }
            else {
            	sellStockMax = sellStockMin;
            }
            
        	logger.info("Parsing sell stock :" + sellStockMin + " " + sellStockMax);
        }
        //Parse buy data
        if(parts.length >= 5) {
        	if(parts[3].contains("mf=")) {
	        	range = parts[3].replace("mf=", "").split("-");
	        	minBuyMF = Double.parseDouble(range[0].trim());
	        	if(range.length > 1) {
	        		maxBuyMF = Double.parseDouble(range[1].trim());
	        	}
	        	else {
	        		maxBuyMF = minBuyMF;
	        	}
	        	logger.info("Parsing buy mf :" + minBuyMF + " " + maxBuyMF);
        	}
        	else {
        		range = parts[3].split("-");
        		fixedBuyPriceMin = Integer.parseInt(range[0].trim());
        		if(range.length > 1) {
        			fixedBuyPriceMax = Integer.parseInt(range[1].trim());
        		}
        		else {
        			fixedBuyPriceMax = fixedBuyPriceMin;
        		}
        		logger.info("Parsing buy fixed price :" + fixedBuyPriceMin + " " + fixedBuyPriceMax);
        	}

    		parts[4] = parts[4].split("#")[0];
    		range = parts[4].split("-");
    		totalStockMin = Integer.parseInt(range[0].trim());
    		if(range.length > 1) {
    			totalStockMax = Integer.parseInt(range[1].trim());
    		}
    		else {
    			totalStockMax = totalStockMin;
    		}
        	logger.info("Parsing total stock :" + totalStockMin + " " + totalStockMax);
        }
        logger.info("Parsed trade with item : " + itemName + " from trader : " + traderName);
        return new ParsedTradeDTO(traderName, itemName, sellStockMin, sellStockMax, totalStockMin, totalStockMax, minSellMF, maxSellMF, minBuyMF, maxBuyMF, fixedSellPriceMin, fixedSellPriceMax, fixedBuyPriceMin, fixedBuyPriceMax);
    }
	
	public static Map<String, GlobalDefConfigEntryDTO> parseGlobalDef(File globalDefFile) throws IOException {
		Map<String, GlobalDefConfigEntryDTO> globalDefConfig = new HashMap<String, GlobalDefConfigEntryDTO>();
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
                	globalDefConfig.put(itemStringID, new GlobalDefConfigEntryDTO(itemStringID, (int)marketPrice));
                    itemStringID = null;
                    marketPrice = 0;
                }
            }
        }
        
		return globalDefConfig;
	}
	
	public static Map<String, String> parseLocalization(File localizationFile) throws IOException {
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
	
	private static Map<String, ParsedItemDTO> parseItemFile(File itemsFile) throws FileNotFoundException, IOException {
		String itemStringID = null;
		String itemGlobalRef = null;
        int marketPrice = 0;
        String line;
        Map<String, ParsedItemDTO> result = new HashMap<String, ParsedItemDTO>();
		
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
                		logger.info("Found item " + itemStringID + " with globalRef " + globalRefMatcher.group(1));
                		itemGlobalRef = globalRefMatcher.group(1);
                }

                if (itemStringID != null && (marketPrice > 0 || itemGlobalRef != null)) {	
                	result.put(itemStringID, new ParsedItemDTO(itemStringID, itemGlobalRef, marketPrice));
                	logger.info("Item : " + itemStringID + " parsed");
                    itemStringID = null;
                    marketPrice = 0;
                    itemGlobalRef = null;
                }
            }
        }
		return result;
	}
}

package com.empyrionatlas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.ItemTradeInfoDTO;
import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.dto.TraderDTO;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.TradeData;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.repository.ItemRepository;
import com.empyrionatlas.repository.TradeRepository;
import com.empyrionatlas.repository.TraderRepository;
import com.empyrionatlas.utils.REConfigParser;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModTradingDataService {
	
	private final TraderRepository traderRepository;
	private final ItemRepository itemRepository;
	private final TradeRepository tradeRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ModTradingDataService.class);

    // Read the file path from application.yml
    @Value("${REConfig.traderFilePath}")
    private String efcTraderInfoFilePath;
    
    @Value("${REConfig.blocksFilePath}")
    private String ecfBlocksInfoFilePath;
    
    @Value("${REConfig.itemFilePath}")
    private String ecfItemsInfoFilePath;
    
    @Value("${REConfig.globalDefPath}")
    private String ecfGlobalDefFilePath;
    
    @Value("${REConfig.LocalizationFilePath}")
    private String csvLocalizationFilePath;


    public ModTradingDataService(TraderRepository traderRepository, ItemRepository itemRepository, TradeRepository tradeRepository) {
        this.traderRepository = traderRepository;
        this.itemRepository = itemRepository;
        this.tradeRepository = tradeRepository;
    }
    
    public List<ItemTradeInfoDTO> getItemTradeData(String itemName) {
    	boolean itemExists = !itemRepository.findByItemName(itemName).isEmpty();
    	List<TradeData> allTradesWithItem = tradeRepository.findByItemName(itemName);
    	List<ItemTradeInfoDTO> result = new ArrayList<ItemTradeInfoDTO>();
    	
    	logger.info("Looking for item : " + itemName);
    	
    	if(!itemExists) {
    		logger.info("item : " + itemName + " is not in the database");
    		return null;
    	}
    	
    	
    	if (allTradesWithItem.isEmpty()) {
    		logger.info("item : " + itemName + " had no trades associated with it");
            return result;
        }
    	
    	for(TradeData trade : allTradesWithItem) {
    		result.add(new ItemTradeInfoDTO(itemName,
    				trade.getAverageSellPrice(),
    				trade.getAverageBuyPrice(),
    				trade.getAverageSellVolume(),
    				trade.getAverageBuyVolume(),
    				new TraderDTO((trade.getTrader() != null ? trade.getTrader().getName() : ""))));
    	}
    	logger.info("Found : " + result.size() + " trades for item : " + itemName);
    	return result;
    }
    
    public void refreshTradingData() {
        logger.info("Refreshing trading data from config...");
        clearTradeData();
        logger.info("Cleared previous data");
        try {
        	processItemsConfigFile(); //Always process items first so trades can reference them
        	
        	Map<String, ItemData> itemCache = itemRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(ItemData::getStringID, item -> item));
        	
            processTraderConfigFile(itemCache);  
            logger.info("Reloaded trading data successfully.");
        } catch (Exception e) {
        	logger.error("Error during trading data refresh: " + e.getMessage());
        }
    }
    
    @PostConstruct
    public void refreshOnStartup() {
    	//refreshTradingData();
    }
    
    public List<String> suggestItemNames(String query) {
    	logger.info("Looking for item suggestions with input: " + query);
    	List<String> suggestedNames = new ArrayList<String>();
        List<ItemData> suggestedItems = itemRepository.findTop5ByItemNameIgnoreCaseContainingOrderByItemNameAsc(query);
        
        for(ItemData item : suggestedItems) {
        	suggestedNames.add(item.getItemName());
        }
        
        return suggestedNames;
    }

    
    private void processItemsConfigFile() throws IOException {
        File itemsConfigFile = new File(ecfItemsInfoFilePath);
        File blocksConfigFile = new File(ecfBlocksInfoFilePath);
        File globalDefFile = new File(ecfGlobalDefFilePath);
        File localizationFile = new File(csvLocalizationFilePath);
        

        if (!itemsConfigFile.exists()) {
            throw new IOException("TraderNPCConfig.ecf not found at : " + ecfItemsInfoFilePath);
        }
        if (!blocksConfigFile.exists()) {
            throw new IOException("BlocksConfig.ecf not found at : " + ecfBlocksInfoFilePath);
        }
        if (!globalDefFile.exists()) {
            throw new IOException("GlobalDefsConfig.ecf not found at : " + ecfGlobalDefFilePath);
        }
        if (!localizationFile.exists()) {
            throw new IOException("Localization.csv not found at : " + csvLocalizationFilePath);
        }
        

        List<ItemData> items = REConfigParser.parseItemConfigFile(itemsConfigFile, blocksConfigFile, globalDefFile, localizationFile);
        for(ItemData item : items) {
        	if (itemRepository.findByStringID(item.getStringID()).isEmpty()) {
        		itemRepository.save(item);
        	}
        }
    }

    private void processTraderConfigFile(Map<String, ItemData> itemCache) throws IOException {
        File traderConfigFile = new File(efcTraderInfoFilePath);

        if (!traderConfigFile.exists()) {
            throw new IOException("TraderNPCConfig.ecf not found at : " + efcTraderInfoFilePath);
        }

        TradeConfigParseResultDTO traders = REConfigParser.parseTraderConfigFile(traderConfigFile, itemCache);
        if (traders != null) {
        	for(TraderData trader : traders.getTraders()) {
        		traderRepository.save(trader);
        	}
        }
    }
    
    private void clearTradeData() {
    	tradeRepository.deleteAll();
        traderRepository.deleteAll();
        itemRepository.deleteAll();
    }
}

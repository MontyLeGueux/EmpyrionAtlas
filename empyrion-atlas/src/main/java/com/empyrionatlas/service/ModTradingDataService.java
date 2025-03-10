package com.empyrionatlas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.repository.ItemRepository;
import com.empyrionatlas.repository.TraderRepository;
import com.empyrionatlas.utils.REConfigParser;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModTradingDataService {
	
	private final TraderRepository traderRepository;
	private final ItemRepository itemRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ModTradingDataService.class);

    // Read the file path from application.yml
    @Value("${REConfig.traderFilePath}")
    private String efcTraderInfoFilePath;
    
    @Value("${REConfig.itemFilePath}")
    private String ecfItemsInfoFilePath;

    public ModTradingDataService(TraderRepository traderRepository, ItemRepository itemRepository) {
        this.traderRepository = traderRepository;
        this.itemRepository = itemRepository;
    }
    
    public void refreshTradingData() {
        logger.info("Refreshing trading data from config...");
        try {
        	processItemsConfigFile(); //Always process items first so trades can reference them
        	
        	Map<String, ItemData> itemCache = itemRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(ItemData::getItemName, item -> item));
        	
            processTraderConfigFile(itemCache);  
            logger.info("Reloaded trading data successfully.");
        } catch (Exception e) {
        	logger.error("Error during trading data refresh: " + e.getMessage());
        }
    }
    
    @PostConstruct
    public void refreshOnStartup() {
    	refreshTradingData();
    }
    
    private void processItemsConfigFile() throws IOException {
        File itemsConfigFile = new File(ecfItemsInfoFilePath);

        if (!itemsConfigFile.exists()) {
            throw new IOException("TraderNPCConfig.ecf not found at : " + ecfItemsInfoFilePath);
        }

        List<ItemData> items = REConfigParser.parseItemConfigFile(itemsConfigFile);
        for(ItemData item : items) {
        	if (itemRepository.findByItemName(item.getItemName()).isEmpty()) {
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
}

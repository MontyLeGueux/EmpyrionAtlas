package com.empyrionatlas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.BlueprintParseResultDTO;
import com.empyrionatlas.dto.ItemTradeInfoDTO;
import com.empyrionatlas.dto.ItemTradeSearchResultDTO;
import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.dto.TraderDTO;
import com.empyrionatlas.dto.TraderInstanceDTO;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.StationData;
import com.empyrionatlas.model.TradeData;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.model.TraderInstanceData;
import com.empyrionatlas.repository.ItemRepository;
import com.empyrionatlas.repository.StationRepository;
import com.empyrionatlas.repository.TradeRepository;
import com.empyrionatlas.repository.TraderInstanceRepository;
import com.empyrionatlas.repository.TraderRepository;
import com.empyrionatlas.utils.EGSBlueprintParser;
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
	private final TraderInstanceRepository traderInstanceRepository;
	private final StationRepository stationRepository;
	
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
    
    @Value("${REConfig.blueprintsFolderPath}")
    private String blueprintsFolderPath;


    public ModTradingDataService(TraderRepository traderRepository, ItemRepository itemRepository, TradeRepository tradeRepository, TraderInstanceRepository traderInstanceRepository, StationRepository stationRepository) {
        this.traderRepository = traderRepository;
        this.itemRepository = itemRepository;
        this.tradeRepository = tradeRepository;
		this.traderInstanceRepository = traderInstanceRepository;
		this.stationRepository = stationRepository;
    }
    
    public ItemTradeSearchResultDTO getItemTradeData(String itemName) {
    	boolean itemExists = !itemRepository.findByItemName(itemName).isEmpty();
    	List<TradeData> allTradesWithItem = tradeRepository.findByItemName(itemName);
    	List<ItemTradeInfoDTO> result = new ArrayList<ItemTradeInfoDTO>();
    	
    	logger.info("Looking for item : " + itemName);
    	
    	if(!itemExists) {
    		logger.info("item : " + itemName + " is not in the database");
    		return new ItemTradeSearchResultDTO(false, null);
    	}
    	
    	
    	if (allTradesWithItem.isEmpty()) {
    		logger.info("item : " + itemName + " had no trades associated with it");
    		return new ItemTradeSearchResultDTO(true, new ArrayList<ItemTradeInfoDTO>());
        }
    	
    	for(TradeData trade : allTradesWithItem) {
    		if(trade.getTrader() != null) {
    			List<TraderInstanceData> traderInstances = traderInstanceRepository.findTraderInstanceByTraderName(trade.getTrader().getName());
    			if(traderInstances.isEmpty()) {
    				logger.info("Trader instances is empty for trader : " + trade.getTrader().getName());
    			}
    			else {
		    		result.add(new ItemTradeInfoDTO(itemName,
		    				trade.getAverageSellPrice(),
		    				trade.getAverageBuyPrice(),
		    				trade.getAverageSellVolume(),
		    				trade.getAverageBuyVolume(),
		    				traderInstances.stream()
		    			    .map(instance -> new TraderInstanceDTO(
		    			    	trade.getTrader().getName(),
		    			        instance.getRestockTimer(),
		    			        instance.getStation().getName()
		    			    ))
		    			    .collect(Collectors.toList())));
    			}
    		}
    	}
    	logger.info("Found : " + result.size() + " trades for item : " + itemName);
    	return new ItemTradeSearchResultDTO(true, result);
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
}

package com.empyrionatlas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.BlueprintParseResultDTO;
import com.empyrionatlas.dto.ItemTradeInfoDTO;
import com.empyrionatlas.dto.ItemTradeSearchResultDTO;
import com.empyrionatlas.dto.ProfitableTradeDTO;
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
    			List<TraderInstanceData> traderInstances = traderInstanceRepository.findTraderInstanceByTraderStringID(trade.getTrader().getStringID());
    			if(traderInstances.isEmpty()) {
    				logger.info("Trader instances is empty for trader : " + trade.getTrader().getStringID());
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
		    			    .distinct()
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
    
    public List<ProfitableTradeDTO> getProfitableTrades() {
        List<TradeData> allTrades = tradeRepository.findAllWithDetails();
        List<ProfitableTradeDTO> result = new ArrayList<>();

        for (TradeData sellerTrade : allTrades) {
            for (TradeData buyerTrade : allTrades) {
                // if we found different traders trading the same item
                if (!sellerTrade.getTrader().equals(buyerTrade.getTrader()) &&
                    sellerTrade.getItem().getStringID().equals(buyerTrade.getItem().getStringID())) {

                    double avgSellPrice = sellerTrade.getAverageSellPrice();
                    double avgBuyPrice = buyerTrade.getAverageBuyPrice();

                    int avgSellStock = sellerTrade.getAverageSellVolume();
                    int sellableAmount = buyerTrade.getAverageBuyVolume();

                    int tradeAmount = Math.min(avgSellStock, sellableAmount);
                    double profitPerItem = avgBuyPrice - avgSellPrice;
                    double totalProfit = profitPerItem * tradeAmount;

                    //if we have a non zero amount we can buy then sell and if it makes a positive amount of money
                    if (tradeAmount > 0 && profitPerItem > 0) {
                    	List<String> buyStations = sellerTrade.getTrader().getLocations().isEmpty()
                            ? new ArrayList<>()
                            : buyerTrade.getTrader().getLocations().stream().map(t -> t.getStation().getName()).distinct().toList();

                    	List<String> sellStations = buyerTrade.getTrader().getLocations().isEmpty()
                            ? new ArrayList<>()
                            : sellerTrade.getTrader().getLocations().stream().map(t -> t.getStation().getName()).distinct().toList();
                    	
                        result.add(new ProfitableTradeDTO(
                            sellerTrade.getItem().getItemName(),
                            buyStations,
                            sellStations,
                            buyerTrade.getTrader().getName(),
                            sellerTrade.getTrader().getName(),
                            avgSellPrice,
                            avgBuyPrice,
                            tradeAmount,
                            totalProfit
                        ));
                    }
                }
            }
        }

        // Sort by most profitable first
        result.sort((a, b) -> Double.compare(b.getTotalProfit(), a.getTotalProfit()));
        return result;
    }
}

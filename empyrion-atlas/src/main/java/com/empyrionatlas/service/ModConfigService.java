package com.empyrionatlas.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.empyrionatlas.dto.BlueprintParseResultDTO;
import com.empyrionatlas.dto.GlobalDefConfigEntryDTO;
import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.dto.TraderInstanceDTO;
import com.empyrionatlas.dto.parsing.ParsedItemDTO;
import com.empyrionatlas.dto.parsing.ParsedStationDTO;
import com.empyrionatlas.dto.parsing.ParsedTraderDTO;
import com.empyrionatlas.mapping.ParsingMapper;
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

@Service
public class ModConfigService {

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
    
    public ModConfigService(TraderRepository traderRepository, ItemRepository itemRepository, TradeRepository tradeRepository, TraderInstanceRepository traderInstanceRepository, StationRepository stationRepository) {
        this.traderRepository = traderRepository;
        this.itemRepository = itemRepository;
        this.tradeRepository = tradeRepository;
		this.traderInstanceRepository = traderInstanceRepository;
		this.stationRepository = stationRepository;
    }
    
    @PostConstruct
    public void refreshOnStartup() {
    	//refreshTradingData();
    }
    
    public void refreshTradingData() {
    	logger.info("Refreshing trading data from config...");
        clearTradeData();
        logger.info("Cleared previous data");
        try {
        	
        	logger.info("Checking for files...");
        	File globalDefFile = new File(ecfGlobalDefFilePath);
            File localizationFile = new File(csvLocalizationFilePath);
            
            File blueprintFolder = new File(blueprintsFolderPath);
            File traderConfigFile = new File(efcTraderInfoFilePath);
            File itemsConfigFile = new File(ecfItemsInfoFilePath);
            File blocksConfigFile = new File(ecfBlocksInfoFilePath);
            
            if (!globalDefFile.exists()) {
                throw new IOException("GlobalDefsConfig.ecf not found at : " + ecfGlobalDefFilePath);
            }
            if (!localizationFile.exists()) {
                throw new IOException("Localization.csv not found at : " + csvLocalizationFilePath);
            }
            
            if (!blueprintFolder.exists()) {
                throw new IOException("Blueprint Folder not found at : " + blueprintsFolderPath);
            }
            if (!traderConfigFile.exists()) {
                throw new IOException("TraderNPCConfig.ecf not found at : " + efcTraderInfoFilePath);
            }
            if (!itemsConfigFile.exists()) {
                throw new IOException("ItemsConfig.ecf not found at : " + ecfItemsInfoFilePath);
            }
            if (!blocksConfigFile.exists()) {
                throw new IOException("BlocksConfig.ecf not found at : " + ecfBlocksInfoFilePath);
            }
            logger.info("All files found");

            Map<String, GlobalDefConfigEntryDTO> globalDefParseResult = REConfigParser.parseGlobalDef(globalDefFile);
            Map<String, String> localizationParseResult = REConfigParser.parseLocalization(localizationFile);
            
            Map<String,ParsedItemDTO> itemsParseResult = REConfigParser.parseItemsAndBlocksConfigFile(itemsConfigFile, blocksConfigFile);
            Map<String,ParsedTraderDTO> parsedTraders = REConfigParser.parseTraderConfigFile(traderConfigFile);
            Map<String,BlueprintParseResultDTO> parsedStationDTO = parseStationsInFolder(blueprintFolder);
            
            logger.info("Parsing finished, saving to database... ");
            
            List<ItemData> items = ParsingMapper.mapItems(itemsParseResult, localizationParseResult, globalDefParseResult);
            itemRepository.saveAll(items);
            
            logger.info("test item : " + items.stream().filter(item -> item.getStringID().equals("Eden_ShipmentFuel")).findFirst());
            
            logger.info("test item 2 : " + itemsParseResult.get("Eden_ShipmentFuel"));

            Map<String, ItemData> itemMap = items.stream()
                .collect(Collectors.toMap(ItemData::getStringID, Function.identity()));
            
            logger.info("Mapped " + items.size() + " items");

            List<TraderData> traders = ParsingMapper.mapTraders(parsedTraders, localizationParseResult);
            traderRepository.saveAll(traders);

            Map<String, TraderData> traderMap = traders.stream()
                .collect(Collectors.toMap(TraderData::getStringID, Function.identity()));
            
            logger.info("Mapped " + traderMap.size() + " traders");

            List<TradeData> trades = ParsingMapper.mapTrades(parsedTraders, itemMap, traderMap);
            tradeRepository.saveAll(trades);
            
            logger.info("Mapped " + trades.size() + " trades");

            List<StationData> stations = ParsingMapper.mapStations(parsedStationDTO, traderMap);
            stationRepository.saveAll(stations);
            
            logger.info("Mapped " + stations.size() + " stations");
            
        } catch (Exception e) {
        	logger.error("Error during trading data refresh: " + e.getMessage());
        }
    }
    
    private Map<String, BlueprintParseResultDTO> parseStationsInFolder(File blueprintFolder) throws IOException{
    	logger.info("Parsing blueprint folder...");
    	File[] blueprints = blueprintFolder.listFiles();
    	Map<String, BlueprintParseResultDTO> result = new HashMap<String, BlueprintParseResultDTO>();
		BlueprintParseResultDTO currentStation = null;
		
		for (File blueprint : blueprints) {
	        if (blueprint.isFile() && blueprint.getName().endsWith(EGSBlueprintParser.BLUEPRINT_FILE_EXTENSION)) {
	        	logger.info("Parsing blueprint file : " + blueprint.getName());
	        	currentStation = EGSBlueprintParser.parseBlueprintFile(blueprint);
	        	if(currentStation != null 
	        			&& currentStation.getBlueprintName() != null 
	        			&& !currentStation.getBlueprintName().isEmpty()
	        			&& currentStation.getBlueprintTraderInstances() != null
	        			&& !currentStation.getBlueprintTraderInstances().isEmpty()) {
	        		result.put(currentStation.getBlueprintName(), currentStation);
	        	}
	        }
	    }
		return result;
    }

    private void processStationBlueprintFiles() throws IOException{
    	File blueprintFolder = new File(blueprintsFolderPath);
    	
    	logger.info("Parsing blueprint folder ...");
    	
    	if(blueprintFolder != null && blueprintFolder.exists()) {
    		File[] blueprints = blueprintFolder.listFiles();
    		BlueprintParseResultDTO parseResult = null;
    		StationData station = null;
    		TraderInstanceData traderInstance = null;
    		TraderData trader = null;
    		
    		for (File blueprint : blueprints) {
    	        if (blueprint.isFile() && blueprint.getName().endsWith(EGSBlueprintParser.BLUEPRINT_FILE_EXTENSION)) {
    	        	logger.info("Parsing blueprint file : " + blueprint.getName());
    	        	parseResult = EGSBlueprintParser.parseBlueprintFile(blueprint);
    	        	if(parseResult != null 
    	        			&& parseResult.getBlueprintName() != null 
    	        			&& !parseResult.getBlueprintName().isEmpty()
    	        			&& parseResult.getBlueprintTraderInstances() != null
    	        			&& !parseResult.getBlueprintTraderInstances().isEmpty()) {
    	        		
    	        		final String stationName = parseResult.getBlueprintName();
    	        		station = stationRepository.findByName(parseResult.getBlueprintName())
    	        			    .orElseGet(() -> {
    	        			    	StationData newStation = new StationData();
    	        			        newStation.setName(stationName);
    	        			        return stationRepository.save(newStation);
    	        			    });
    	        		
    	        		for (TraderInstanceDTO traderInstanceDTO : parseResult.getBlueprintTraderInstances()) {
    	        		   trader = traderRepository.findByStringID(traderInstanceDTO.getTraderName()).orElse(null);
    	        		   
    	        		    if(trader != null) {
    	        		    	traderInstance = new TraderInstanceData();
    	        		    	traderInstance.setStation(station);
    	        		    	traderInstance.setTrader(trader);
    	        		    	traderInstance.setRestockTimer(traderInstanceDTO.getRestockTimer());

        	        		    traderInstanceRepository.save(traderInstance);
    	        		    }
    	        		    else {
    	        		    	logger.info("Couldn't find trader : " + traderInstanceDTO.getTraderName() + " in station : " + station.getName());
    	        		    }
    	        		}
    	        	}
    	        }
    	    }
    	}
    }
    
    private void clearTradeData() {
    	tradeRepository.deleteAll();
        traderRepository.deleteAll();
        itemRepository.deleteAll();
		traderInstanceRepository.deleteAll();
		stationRepository.deleteAll();
    }
    
}

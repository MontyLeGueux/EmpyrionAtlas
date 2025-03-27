package com.empyrionatlas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empyrionatlas.dto.ItemTradeInfoDTO;
import com.empyrionatlas.dto.ItemTradeSearchResultDTO;
import com.empyrionatlas.service.ModConfigService;
import com.empyrionatlas.service.ModTradingDataService;

@RestController
@RequestMapping("/api/items")
public class ItemInformationController {
	
	private final ModTradingDataService modTradingDataService;
	private final ModConfigService modConfigService;

	public ItemInformationController(ModTradingDataService modTradingDataService, ModConfigService modConfigService) {
        this.modTradingDataService = modTradingDataService;
        this.modConfigService = modConfigService;
    }

    @GetMapping("/check")
    public String checkService() {
        return "Service is running!";
    }
    
    @GetMapping("/{itemName}")
    public ResponseEntity<ItemTradeSearchResultDTO> getItemTradeData(@PathVariable String itemName) {
    	ItemTradeSearchResultDTO searchResult = modTradingDataService.getItemTradeData(itemName);
        
        if(searchResult == null) {
        	return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(searchResult);
    }
    
    @GetMapping("/DebugParseConfig")
    public String debugParseConfig() {
    	modConfigService.refreshTradingData();
        return "Refreshing database and parsing config";
    }
    
    @GetMapping("/suggest")
    public ResponseEntity<List<String>> suggestItems(@RequestParam String query) {
        List<String> suggestions = modTradingDataService.suggestItemNames(query);
        return ResponseEntity.ok(suggestions);
    }
}
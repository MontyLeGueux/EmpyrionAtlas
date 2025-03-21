package com.empyrionatlas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empyrionatlas.dto.ItemTradeInfoDTO;
import com.empyrionatlas.service.ModTradingDataService;

@RestController
@RequestMapping("/api/items")
public class TradingInformationController {
	
	private final ModTradingDataService modTradingDataService;

	public TradingInformationController(ModTradingDataService modTradingDataService) {
        this.modTradingDataService = modTradingDataService;
    }

    @GetMapping("/check")
    public String checkService() {
        return "Service is running!";
    }
    
    @GetMapping("/{itemName}")
    public ResponseEntity<List<ItemTradeInfoDTO>> getItemTradeData(@PathVariable String itemName) {
        List<ItemTradeInfoDTO> itemTradeDTO = modTradingDataService.getItemTradeData(itemName);
        
        if(itemTradeDTO == null) {
        	return ResponseEntity.notFound().build();
        }
        
        if (itemTradeDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(itemTradeDTO);
    }
    
    @GetMapping("/DebugParseConfig")
    public String debugParseConfig() {
    	modTradingDataService.refreshTradingData();
        return "Refreshing database and parsing config";
    }
}

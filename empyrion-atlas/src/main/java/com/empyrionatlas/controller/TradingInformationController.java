package com.empyrionatlas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empyrionatlas.service.ModTradingDataService;

@RestController
@RequestMapping("/api/test")
public class TradingInformationController {
	
	private final ModTradingDataService modTradingDataService;

	public TradingInformationController(ModTradingDataService modTradingDataService) {
        this.modTradingDataService = modTradingDataService;
    }

    @GetMapping("/check")
    public String checkService() {
        return "Service is running!";
    }
}

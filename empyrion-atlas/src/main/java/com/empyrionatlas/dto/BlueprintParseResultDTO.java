package com.empyrionatlas.dto;

import java.util.List;

public class BlueprintParseResultDTO {
	
	private String blueprintName;
	
	private List<String> blueprintTraderNames;
	
    public BlueprintParseResultDTO(String blueprintName, List<String> blueprintTraderNames) {
        this.blueprintName = blueprintName;
        this.blueprintTraderNames = blueprintTraderNames;
    }

    public String getBlueprintName() { return blueprintName; }
    
    public List<String> getBlueprintTraderNames() { return blueprintTraderNames; }
}

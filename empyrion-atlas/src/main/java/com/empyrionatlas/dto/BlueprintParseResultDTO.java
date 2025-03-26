package com.empyrionatlas.dto;

import java.util.List;

public class BlueprintParseResultDTO {
	
	private String blueprintName;
	
	private List<TraderInstanceDTO> blueprintTraderInstances;
	
    public BlueprintParseResultDTO(String blueprintName, List<TraderInstanceDTO> blueprintTraderInstances) {
        this.blueprintName = blueprintName;
        this.blueprintTraderInstances = blueprintTraderInstances;
    }

    public String getBlueprintName() { return blueprintName; }
    
    public List<TraderInstanceDTO> getBlueprintTraderInstances() { return blueprintTraderInstances; }
}

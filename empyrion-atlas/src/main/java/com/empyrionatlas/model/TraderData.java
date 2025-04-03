package com.empyrionatlas.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "traders")
public class TraderData {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String stringID;
	private String name;
	
	@OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TradeData> itemsForSale = new ArrayList<>();
	
	@OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TraderInstanceData> locations = new ArrayList<>();
	
	public TraderData() {}

    public TraderData(String stringID, String name, List<TradeData> itemsForSale, List<TraderInstanceData> locations) { 
    	this.stringID = stringID;
    	this.name = name;
    	this.itemsForSale = itemsForSale;
    	this.locations = locations;
    }

	public void setStringID(String stringID) { this.stringID = stringID; }
	public String getStringID() { return stringID; }

	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
	
	public List<TradeData> getItemsForSale() { return itemsForSale; }
    public void setItemsForSale(List<TradeData> itemsForSale) { this.itemsForSale = itemsForSale; }
	
	public List<TraderInstanceData> getLocations() { return locations; }
    public void setLocations(List<TraderInstanceData> locations) { this.locations = locations; }
}

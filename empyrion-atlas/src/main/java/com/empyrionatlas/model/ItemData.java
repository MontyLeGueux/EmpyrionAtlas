package com.empyrionatlas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class ItemData {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false)
    private String stringID;
	
	@Column(nullable = true)
    private String itemName;
	
	@Column()
    private double basePrice;
	
	public ItemData() {}

    public ItemData(String stringID, double basePrice, String itemName) {
        this.stringID = stringID;
        this.basePrice = basePrice;
        this.itemName = itemName;
    }

    public String getStringID() { return stringID; }
    public void setStringID(String stringID) { this.stringID = stringID; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
}

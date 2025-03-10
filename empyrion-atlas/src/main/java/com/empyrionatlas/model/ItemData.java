package com.empyrionatlas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class ItemData {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false, unique = true)
    private String itemName;
	
	@Column()
    private double basePrice;
	
	public ItemData() {}

    public ItemData(String itemName, double basePrice) {
        this.itemName = itemName;
        this.basePrice = basePrice;
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
}

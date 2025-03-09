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
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TradingItemData> itemsForSale = new ArrayList<>();
	
	public TraderData() {}

    public TraderData(String name) {
        this.name = name;
    }

	public void setItemsForSale(List<TradingItemData> items) {
		this.itemsForSale = items;
	}

	public void setName(String name) {
		this.name = name;
	}

}

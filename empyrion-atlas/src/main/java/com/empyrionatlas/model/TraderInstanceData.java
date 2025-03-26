package com.empyrionatlas.model;

import jakarta.persistence.*;

//Used to model the trader to station relation
@Entity
@Table(name = "trader_instances")
public class TraderInstanceData {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StationData station;

    @ManyToOne
    private TraderData trader;
    
    @Column(name = "restock_timer")
    private Integer restockTimer;
    
    public TraderInstanceData() {}

    public TraderInstanceData(StationData station, TraderData trader, int restockTimer) {
        this.station = station;
        this.trader = trader;
        this.restockTimer = restockTimer;
    }

    public StationData getStation() { return station; }
    public void setStation(StationData station) { this.station = station; }

    public TraderData getTrader() { return trader; }
    public void setTrader(TraderData trader) { this.trader = trader; }
    
    public int getRestockTimer() { return restockTimer; }
    public void setRestockTimer(int restockTimer) { this.restockTimer = restockTimer; }
}

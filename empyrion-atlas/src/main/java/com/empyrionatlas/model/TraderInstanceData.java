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
    
    public TraderInstanceData() {}

    public TraderInstanceData(StationData station, TraderData trader) {
        this.station = station;
        this.trader = trader;
    }

    public StationData getStation() { return station; }
    public void setStation(StationData station) { this.station = station; }

    public TraderData getTrader() { return trader; }
    public void setTrader(TraderData trader) { this.trader = trader; }
}

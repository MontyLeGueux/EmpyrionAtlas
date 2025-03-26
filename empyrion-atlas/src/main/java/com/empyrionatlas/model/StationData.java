package com.empyrionatlas.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class StationData {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false)
    private String name;
	
	@OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TraderInstanceData> traderInstances = new ArrayList<>();
	
	public StationData() {}

    public StationData(String name, List<TraderInstanceData> traderInstances) {
        this.name = name;
        this.traderInstances = traderInstances;
    }
    
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<TraderInstanceData> getTraderInstances() { return traderInstances; }
    public void setTraderInstances(List<TraderInstanceData> traderInstances) { this.traderInstances = traderInstances; }
}

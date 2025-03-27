package com.empyrionatlas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empyrionatlas.model.StationData;
import com.empyrionatlas.model.TraderInstanceData;

public interface TraderInstanceRepository extends JpaRepository<TraderInstanceData, Long>{

	@Query("SELECT ti.station FROM TraderInstanceData ti WHERE ti.trader.name = :name")
	List<StationData> findStationsByTraderName(@Param("name") String name);
	
	List<TraderInstanceData> findTraderInstancByTraderName(@Param("name") String name); 

}

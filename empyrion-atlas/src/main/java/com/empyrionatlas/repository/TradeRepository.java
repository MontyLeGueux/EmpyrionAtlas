package com.empyrionatlas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.empyrionatlas.model.TradeData;

@Repository
public interface TradeRepository  extends JpaRepository<TradeData, Long>{
	@Query("SELECT i FROM TradeData i WHERE i.item.itemName = :itemName")
	List<TradeData> findByItemName(String itemName);
	
	@Query("SELECT t FROM TradeData t JOIN FETCH t.item i JOIN FETCH t.trader tr JOIN FETCH tr.locations l JOIN FETCH l.station s")
	List<TradeData> findAllWithDetails();
}

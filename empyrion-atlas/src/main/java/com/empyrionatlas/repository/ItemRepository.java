package com.empyrionatlas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.empyrionatlas.model.TradingItemData;

@Repository
public interface ItemRepository extends JpaRepository<TradingItemData, Long>{
	List<TradingItemData> findByItemName(String itemName);

}

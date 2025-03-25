package com.empyrionatlas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.empyrionatlas.model.ItemData;

@Repository
public interface ItemRepository extends JpaRepository<ItemData, Long>{

	Optional<ItemData> findByStringID(String stringID);
	List<ItemData> findByItemName(String itemName);
	
	List<ItemData> findTop5ByItemNameIgnoreCaseContainingOrderByItemNameAsc(String query);
}

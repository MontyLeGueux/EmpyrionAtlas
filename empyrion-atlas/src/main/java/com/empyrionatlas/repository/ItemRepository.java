package com.empyrionatlas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.empyrionatlas.model.ItemData;

@Repository
public interface ItemRepository extends JpaRepository<ItemData, Long>{

	Optional<ItemData> findByItemName(String itemName);
}

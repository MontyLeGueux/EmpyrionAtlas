package com.empyrionatlas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empyrionatlas.model.StationData;

public interface StationRepository  extends JpaRepository<StationData, Long>{
	Optional<StationData> findByName(String blueprintName);
	
}

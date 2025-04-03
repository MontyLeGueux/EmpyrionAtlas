package com.empyrionatlas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empyrionatlas.model.TraderData;

public interface TraderRepository extends JpaRepository<TraderData, Long>{
	Optional<TraderData> findByStringID(String name);
}

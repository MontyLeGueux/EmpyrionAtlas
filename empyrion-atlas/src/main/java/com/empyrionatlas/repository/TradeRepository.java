package com.empyrionatlas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.empyrionatlas.model.TradeData;

@Repository
public interface TradeRepository  extends JpaRepository<TradeData, Long>{

}

package com.empyrionatlas.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.BlueprintParseResultDTO;
import com.empyrionatlas.dto.GlobalDefConfigEntryDTO;
import com.empyrionatlas.dto.parsing.ParsedItemDTO;
import com.empyrionatlas.dto.parsing.ParsedTradeDTO;
import com.empyrionatlas.dto.parsing.ParsedTraderDTO;
import com.empyrionatlas.model.ItemData;
import com.empyrionatlas.model.StationData;
import com.empyrionatlas.model.TradeData;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.model.TraderInstanceData;
import com.empyrionatlas.service.ModTradingDataService;

public class ParsingMapper {
	
	private static final Logger logger = LoggerFactory.getLogger(ModTradingDataService.class);

	public static List<ItemData> mapItems(Map<String, ParsedItemDTO> itemsMap, Map<String, String> localizationMap,
			Map<String, GlobalDefConfigEntryDTO> globalDefs) {
		return itemsMap.values().stream().map(dto -> {
			ItemData item = new ItemData();
			item.setStringID(dto.getStringID());

			double basePrice = dto.getBasePrice();
			if (dto.getGlobalRef() != null) {
				GlobalDefConfigEntryDTO def = globalDefs.get(dto.getGlobalRef());
				if (def != null) {
					basePrice = def.getMarketPrice();
				}
			}
			item.setBasePrice(basePrice);

			item.setItemName(localizationMap.getOrDefault(dto.getStringID(), dto.getStringID()));
			return item;
		}).collect(Collectors.toList());
	}

	public static List<TraderData> mapTraders(Map<String, ParsedTraderDTO> tradersMap, Map<String, String> localizationMap) {
		return tradersMap.values().stream().map(dto -> {
			TraderData trader = new TraderData();
			trader.setStringID(dto.getStringID());
			trader.setName(localizationMap.getOrDefault(dto.getStringID(), dto.getStringID()));
			return trader;
		}).collect(Collectors.toList());
	}

	public static List<TradeData> mapTrades(Map<String, ParsedTraderDTO> parsedTraders, Map<String, ItemData> itemEntities,
			Map<String, TraderData> traderEntities) {
		List<TradeData> trades = new ArrayList<>();

		for (ParsedTraderDTO traderDTO : parsedTraders.values()) {
			logger.info("Mapping " + traderDTO.getTrades().size() + " trades for trader : " + traderDTO.getStringID());
			TraderData trader = traderEntities.get(traderDTO.getStringID());
			if (trader == null) {
				logger.info("Missing trader " + traderDTO.getStringID());
				continue;
			}

			for (ParsedTradeDTO tradeDTO : traderDTO.getTrades()) {
				ItemData item = itemEntities.get(tradeDTO.getItemStringID());
				if (item == null) {
					logger.info("Missing item " + tradeDTO.getItemStringID());
					continue;
				}

				TradeData trade = new TradeData();
				trade.setTrader(trader);
				trade.setItem(item);

				trade.setSellStockMin(tradeDTO.getSellStockMin());
				trade.setSellStockMax(tradeDTO.getSellStockMax());
				trade.setTotalStockMin(tradeDTO.getTotalStockMin());
				trade.setTotalStockMax(tradeDTO.getTotalStockMax());

				trade.setSellMarketFluctuationMin(tradeDTO.getSellMarketFluctuationMin());
				trade.setSellMarketFluctuationMax(tradeDTO.getSellMarketFluctuationMax());
				trade.setBuyMarketFluctuationMin(tradeDTO.getBuyMarketFluctuationMin());
				trade.setBuyMarketFluctuationMax(tradeDTO.getBuyMarketFluctuationMax());

				trade.setFixedSellPriceMin(tradeDTO.getFixedSellPriceMin());
				trade.setFixedSellPriceMax(tradeDTO.getFixedSellPriceMax());
				trade.setFixedBuyPriceMin(tradeDTO.getFixedBuyPriceMin());
				trade.setFixedBuyPriceMax(tradeDTO.getFixedBuyPriceMax());

				trades.add(trade);
			}
		}
		return trades;
	}

	public static List<StationData> mapStations(Map<String, BlueprintParseResultDTO> stationDTOs,
			Map<String, TraderData> traderEntities) {
		List<StationData> stations = new ArrayList<>();

		for (BlueprintParseResultDTO dto : stationDTOs.values()) {
			StationData station = new StationData();
			station.setName(dto.getBlueprintName());

			List<TraderInstanceData> instances = dto.getBlueprintTraderInstances().stream().map(instanceDTO -> {
				TraderData trader = traderEntities.get(instanceDTO.getTraderName());
				if (trader == null)
					return null;

				TraderInstanceData instance = new TraderInstanceData();
				instance.setTrader(trader);
				instance.setRestockTimer(instanceDTO.getRestockTimer());
				instance.setStation(station);
				return instance;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			station.setTraderInstances(instances);
			stations.add(station);
		}

		return stations;
	}
}

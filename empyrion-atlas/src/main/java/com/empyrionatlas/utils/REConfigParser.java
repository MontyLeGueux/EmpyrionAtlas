package com.empyrionatlas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.empyrionatlas.dto.TradeConfigParseResultDTO;
import com.empyrionatlas.model.TraderData;
import com.empyrionatlas.model.TradingItemData;

public class REConfigParser {

	public static TradeConfigParseResultDTO parseEcfFile(File ecfFile) throws IOException {
        List<TraderData> traders = new ArrayList<>();
        TraderData currentTrader = null;
        List<TradingItemData> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ecfFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Ignore comments and empty lines
                }

                if (line.startsWith("{")) {
                    if (currentTrader != null) {
                        currentTrader.setItemsForSale(items);
                        traders.add(currentTrader); // Save the previous trader before starting a new one
                    }
                    currentTrader = new TraderData();
                    items = new ArrayList<>();
                    continue;
                }

                if (line.startsWith("}")) {
                    if (currentTrader != null) {
                        currentTrader.setItemsForSale(items);
                        traders.add(currentTrader); // Add the last trader in the file
                    }
                    break; // End of a trader block
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim().replaceAll("\"", ""); // Remove quotes

                if (currentTrader != null) {
                    switch (key) { //add extra trader values here as needed.
                        case "Trader Name" -> currentTrader.setName(value);
                        default -> {
                            if (key.startsWith("Item")) {
                                items.add(parseItem(value, currentTrader));
                            }
                        }
                    }
                }
            }
        }
		return new TradeConfigParseResultDTO(traders);
	}
	
	private static TradingItemData parseItem(String itemLine, TraderData trader) {
        String[] parts = itemLine.split(",");
        String itemName = parts[0].trim();

        double minPrice = 0, maxPrice = 0;
        int minQuantity = 0, maxQuantity = 0;

        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("mf=")) {
                String[] range = part.replace("mf=", "").split("-");
                minPrice = Double.parseDouble(range[0]);
                maxPrice = Double.parseDouble(range[1]);
            } else if (part.matches("\\d+-\\d+")) {
                String[] range = part.split("-");
                minQuantity = Integer.parseInt(range[0]);
                maxQuantity = Integer.parseInt(range[1]);
            }
        }

        return new TradingItemData(itemName, minPrice, maxPrice, minQuantity, maxQuantity, trader);
    }
}

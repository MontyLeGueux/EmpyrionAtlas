package com.empyrionatlas.dto;

import java.util.List;

import com.empyrionatlas.model.TraderData;

public class TradeConfigParseResultDTO {

	private List<TraderData> traders;

    public TradeConfigParseResultDTO(List<TraderData> traders) {
        this.traders = traders;
    }

    public List<TraderData> getTraders() {
        return traders;
    }

    public void setTraders(List<TraderData> traders) {
        this.traders = traders;
    }
}

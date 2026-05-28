package com.amichaimarcus.investment.dto;

import lombok.Data;

@Data
public class AiPredictionDto {
    private String symbol;
    private String timestamp;
    private double current_price;
    private double predicted_next_close;
    private String predicted_trend;
    private String recommended_action;
}
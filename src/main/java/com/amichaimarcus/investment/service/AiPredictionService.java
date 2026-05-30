package com.amichaimarcus.investment.service;

import com.amichaimarcus.investment.dto.AiPredictionDto;
import com.amichaimarcus.investment.model.PredictionHistory;
import com.amichaimarcus.investment.repository.PredictionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AiPredictionService {

    @Autowired
    private PredictionHistoryRepository predictionHistoryRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String AI_SERVER_URL = "https://lstm-pyton-eu.onrender.com/api/ai/predict/";

    // הוספנו את ה-timeframe לסוגריים כדי שהפונקציה תכיר אותו!
    public AiPredictionDto getPredictionForSymbol(String symbol, String timeframe) {
        String url = AI_SERVER_URL + symbol + "?timeframe=" + timeframe;
        AiPredictionDto dto = restTemplate.getForObject(url, AiPredictionDto.class);

        // אם התקבלה תחזית תקינה משרת ה-AI, נשמור אותה בהיסטוריה
        if (dto != null) {
            PredictionHistory history = new PredictionHistory();
            history.setSymbol(dto.getSymbol());
            history.setCurrentPrice(dto.getCurrent_price());
            history.setPredictedPrice(dto.getPredicted_next_close());
            history.setTrend(dto.getPredicted_trend());
            history.setRecommendedAction(dto.getRecommended_action());

            // המרת מחרוזת הזמן מפייתון ל-LocalDateTime של Java
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            history.setPredictionTime(LocalDateTime.parse(dto.getTimestamp(), formatter));

            // הגדרת סטטוס ראשוני כ-PENDING
            history.setStatus("PENDING");

            // שמירה ל-MySQL
            predictionHistoryRepository.save(history);
        }

        return dto;
    }

    // שליפת ההיסטוריה מהמסד
    public List<PredictionHistory> getHistoryForSymbol(String symbol) {
        return predictionHistoryRepository.findBySymbolOrderByPredictionTimeDesc(symbol.toUpperCase());
    }
}

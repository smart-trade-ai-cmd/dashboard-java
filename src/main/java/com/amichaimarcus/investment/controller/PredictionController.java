package com.amichaimarcus.investment.controller;

import com.amichaimarcus.investment.dto.AiPredictionDto;
import com.amichaimarcus.investment.model.PredictionHistory;
import com.amichaimarcus.investment.service.AiPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@CrossOrigin(origins = "*") // מאפשר ל-Frontend לגשת ללא שגיאות אבטחה
public class PredictionController {

    @Autowired
    private AiPredictionService aiPredictionService;

    // כאשר משתמש יבקש תחזית, הבקשה תנותב אוטומטית לשירות שפונה לפייתון ותישמר ב-DB
    @GetMapping("/{symbol}")
    public AiPredictionDto getPrediction(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1d", required = false) String timeframe) {
        // עכשיו ה-Controller יודע לקבל גם את הזמן (ואם לא ציינו, ברירת המחדל היא 1d)
        return aiPredictionService.getPredictionForSymbol(symbol, timeframe);
    }

    // נקודת קצה לקבלת כל היסטוריית התחזיות שנשמרו במסד הנתונים עבור מטבע מסוים
    @GetMapping("/history/{symbol}")
    public List<PredictionHistory> getPredictionHistory(@PathVariable String symbol) {
        return aiPredictionService.getHistoryForSymbol(symbol);
    }
}

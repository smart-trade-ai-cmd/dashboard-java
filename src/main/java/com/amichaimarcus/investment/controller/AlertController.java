package com.amichaimarcus.investment.controller;

import com.amichaimarcus.investment.model.Alert;
import com.amichaimarcus.investment.model.Asset;
import com.amichaimarcus.investment.model.User;
import com.amichaimarcus.investment.repository.AlertRepository;
import com.amichaimarcus.investment.repository.AssetRepository;
import com.amichaimarcus.investment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssetRepository assetRepository;

    // יצירת התראה חדשה
    @PostMapping("/add")
    public Alert createAlert(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        String symbol = payload.get("symbol").toString();
        Double targetPrice = Double.valueOf(payload.get("targetPrice").toString());
        String conditionType = payload.get("conditionType").toString(); // ABOVE או BELOW

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Asset asset = assetRepository.findBySymbol(symbol).orElseThrow(() -> new RuntimeException("Asset not found"));

        Alert alert = new Alert();
        alert.setUser(user);
        alert.setAsset(asset);
        alert.setTargetPrice(targetPrice);
        alert.setConditionType(conditionType);
        alert.setTriggered(false);

        return alertRepository.save(alert);
    }

    // שליפת התראות פעילות של משתמש מסוים
    @GetMapping("/user/{userId}")
    public List<Alert> getActiveAlerts(@PathVariable Long userId) {
        return alertRepository.findByUserId(userId);
    }
}
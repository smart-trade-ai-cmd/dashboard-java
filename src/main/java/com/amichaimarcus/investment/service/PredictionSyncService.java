package com.amichaimarcus.investment.service;

import com.amichaimarcus.investment.model.Alert; // הייבוא שהיה חסר!
import com.amichaimarcus.investment.model.PredictionHistory;
import com.amichaimarcus.investment.repository.AlertRepository; // הייבוא שהיה חסר!
import com.amichaimarcus.investment.repository.PredictionHistoryRepository;
import com.amichaimarcus.investment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class PredictionSyncService {

    @Autowired
    private PredictionHistoryRepository predictionHistoryRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * תפקיד 1: סנכרון תחזיות AI
     */
    @Scheduled(fixedRate = 1800000)
    public void syncPendingPredictionsAutomatically() {
        System.out.println("🤖 [שירות סנכרון אוטומטי] סורק מסד נתונים עבור תחזיות בהמתנה (PENDING)...");

        List<PredictionHistory> allPredictions = predictionHistoryRepository.findAll();

        for (PredictionHistory prediction : allPredictions) {
            if ("PENDING".equals(prediction.getStatus())) {

                LocalDateTime targetTime = prediction.getPredictionTime().plusHours(4);

                if (targetTime.isBefore(LocalDateTime.now())) {
                    try {
                        System.out.println("⏳ נמצאה תחזית שהגיע זמן ההכרעה שלה (מזהה: " + prediction.getId() + "). שולף מחיר היסטורי...");

                        double actualPrice = fetchBinanceHistoricalPrice(prediction.getSymbol(), targetTime);
                        prediction.setActualPrice(actualPrice);

                        boolean isCorrect = false;
                        if ("UP".equals(prediction.getTrend()) && actualPrice > prediction.getCurrentPrice()) {
                            isCorrect = true;
                        } else if ("DOWN".equals(prediction.getTrend()) && actualPrice < prediction.getCurrentPrice()) {
                            isCorrect = true;
                        }

                        prediction.setStatus(isCorrect ? "CORRECT" : "WRONG");
                        predictionHistoryRepository.save(prediction);

                        System.out.println("✅ רשומה " + prediction.getId() + " עודכנה בהצלחה לסטטוס: " + prediction.getStatus());

                    } catch (Exception e) {
                        System.err.println("❌ נכשל סנכרון רשומה " + prediction.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("🤖 [שירות סנכרון אוטומטי] סריקת הרשומות התקופתית הסתיימה.");
    }

    /**
     * תפקיד 2: בדיקת התראות מחיר ושליחת אימייל
     */
    @Scheduled(fixedRate = 60000)
    public void checkStockAlertsAutomatically() {
        List<Alert> activeAlerts = alertRepository.findByIsTriggeredFalse();
        if (activeAlerts.isEmpty()) return;

        System.out.println("🔔 [שירות התראות] סורק " + activeAlerts.size() + " התראות מחיר פעילות...");

        for (Alert alert : activeAlerts) {
            try {
                double currentMarketPrice = 0.0;
                String symbol = alert.getAsset().getSymbol().toUpperCase();

                if (symbol.equals("BTC") || symbol.equals("ETH")) {
                    String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol + "USDT";
                    Map<String, String> res = restTemplate.getForObject(url, Map.class);
                    if (res != null && res.containsKey("price")) {
                        currentMarketPrice = Double.parseDouble(res.get("price"));
                    }
                } else {
                    String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?interval=1m&range=1d";
                    Map<String, Object> res = restTemplate.getForObject(url, Map.class);

                    if (res != null && res.containsKey("chart")) {
                        Map<String, Object> chart = (Map<String, Object>) res.get("chart");
                        List<Map<String, Object>> result = (List<Map<String, Object>>) chart.get("result");
                        if (result != null && !result.isEmpty()) {
                            Map<String, Object> meta = (Map<String, Object>) result.get(0).get("meta");
                            currentMarketPrice = ((Number) meta.get("regularMarketPrice")).doubleValue();
                        }
                    }
                }

                if (currentMarketPrice > 0) {
                    boolean shouldTrigger = false;
                    if ("ABOVE".equals(alert.getConditionType()) && currentMarketPrice >= alert.getTargetPrice()) {
                        shouldTrigger = true;
                    } else if ("BELOW".equals(alert.getConditionType()) && currentMarketPrice <= alert.getTargetPrice()) { // תוקן ל-getConditionType()
                        shouldTrigger = true;
                    }

                    if (shouldTrigger) {
                        alert.setTriggered(true);
                        alertRepository.save(alert); // תוקן מ-stockAlertRepository
                        System.out.println("🚨 [התראה הופעלה!] הנכס " + symbol + " הגיע למחיר " + currentMarketPrice);

                        // קריאה לפונקציית שליחת המייל
                        sendEmailNotification(alert.getUser().getId(), symbol, currentMarketPrice, alert.getTargetPrice(), alert.getConditionType());
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ שגיאה בבדיקת התראה עבור " + alert.getAsset().getSymbol() + ": " + e.getMessage()); // תוקן מ-alert.getSymbol()
            }
        }
    }

    /**
     * פונקציית עזר לשליחת אימייל למשתמש
     */
    private void sendEmailNotification(Long userId, String symbol, double currentPrice, double targetPrice, String criteria) {
        try {
            userRepository.findById(userId).ifPresent(user -> {
                String userEmail = user.getEmail();

                if (userEmail != null && !userEmail.isEmpty()) {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(userEmail);
                    message.setSubject("🚨 התראת מחיר: המטרה הושגה! - Smart Trade AI");

                    String conditionText = criteria.equals("ABOVE") ? "עלה מעל" : "ירד מתחת";
                    String text = "שלום " + user.getUsername() + ",\n\n" +
                            "ההתראה שהגדרת במערכת הופעלה בהצלחה!\n" +
                            "הנכס " + symbol + " " + conditionText + " למחיר היעד שהגדרת: $" + targetPrice + ".\n" +
                            "המחיר הנוכחי בשוק הוא: $" + currentPrice + ".\n\n" +
                            "המשך מסחר מוצלח,\n" +
                            "צוות Smart Trade AI 🤖";

                    message.setText(text);
                    mailSender.send(message);
                    System.out.println("📧 מייל התראה נשלח בהצלחה לכתובת: " + userEmail);
                }
            });
        } catch (Exception e) {
            System.err.println("❌ נכשל שליחת מייל התראה למשתמש מזהה " + userId + ": " + e.getMessage());
        }
    }

    /**
     * פונקציית עזר למשיכת מחיר היסטורי מ-Binance
     */
    private double fetchBinanceHistoricalPrice(String symbol, LocalDateTime targetTime) {
        long timestampMs = targetTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol.toUpperCase() + "USDT&interval=4h&startTime=" + timestampMs + "&limit=1";

        Object[][] response = restTemplate.getForObject(url, Object[][].class);

        if (response != null && response.length > 0) {
            String closePriceStr = (String) response[0][4];
            return Double.parseDouble(closePriceStr);
        }

        throw new RuntimeException("לא נמצאו נתונים היסטוריים בבורסה עבור הזמן המבוקש");
    }
}
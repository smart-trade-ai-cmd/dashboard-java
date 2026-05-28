package com.amichaimarcus.investment.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "current_price", nullable = false)
    private double currentPrice;

    @Column(name = "predicted_price", nullable = false)
    private double predictedPrice;

    @Column(nullable = false)
    private String trend;

    @Column(name = "recommended_action", nullable = false)
    private String recommendedAction;

    @Column(name = "prediction_time", nullable = false)
    private LocalDateTime predictionTime;

    // --- העמודות החדשות שביקשת ---

    @Column(name = "actual_price") // יכול להיות null בהתחלה
    private Double actualPrice;

    @Column(nullable = false) // יתחיל כ-"PENDING" ויהפוך ל-"CORRECT"/"WRONG" בהמשך
    private String status = "PENDING";
}
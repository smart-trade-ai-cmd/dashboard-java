package com.amichaimarcus.investment.repository;

import com.amichaimarcus.investment.model.PredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {

    // שליפת היסטוריית התחזיות של מטבע מסוים, מסודר מהתחזית החדשה ביותר לישנה ביותר
    List<PredictionHistory> findBySymbolOrderByPredictionTimeDesc(String symbol);
}
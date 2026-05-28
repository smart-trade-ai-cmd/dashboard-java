package com.amichaimarcus.investment.repository;

import com.amichaimarcus.investment.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    // שליפת כל שורות המעקב המשויכות לקוד משתמש ספציפי
    List<Watchlist> findByUserId(Long userId);

    // בדיקה בבסיס הנתונים אם קיים כבר קשר בין משתמש מסוים למטבע מסוים
    boolean existsByUserIdAndAssetId(Long userId, Long assetId);

    @jakarta.transaction.Transactional
    void deleteByUserIdAndAssetId(Long userId, Long assetId);
}

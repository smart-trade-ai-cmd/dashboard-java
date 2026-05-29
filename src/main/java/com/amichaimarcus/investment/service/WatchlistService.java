package com.amichaimarcus.investment.service;

import com.amichaimarcus.investment.model.Asset;
import com.amichaimarcus.investment.model.User;
import com.amichaimarcus.investment.model.Watchlist;
import com.amichaimarcus.investment.repository.AssetRepository;
import com.amichaimarcus.investment.repository.UserRepository;
import com.amichaimarcus.investment.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    // הוספת מטבע/מניה לרשימת המעקב של משתמש ספציפי
    public Watchlist addAssetToWatchlist(Long userId, String symbol) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // *** התיקון החכם: מחפשים את הנכס, ואם הוא לא קיים - יוצרים אותו אוטומטית! ***
        Asset asset = assetRepository.findBySymbol(symbol.toUpperCase()).orElse(null);
        
        if (asset == null) {
            asset = new Asset();
            asset.setSymbol(symbol.toUpperCase());
            asset.setName(symbol.toUpperCase()); // נותן למניה שם ראשוני זהה לסימול
            asset = assetRepository.save(asset); // שומר את המניה החדשה במסד הנתונים
        }

        // מניעת כפילויות - שלא יהיה ניתן להוסיף את אותו המטבע לאותו המשתמש
        if (watchlistRepository.existsByUserIdAndAssetId(userId, asset.getId())) {
            throw new RuntimeException("Asset already exists in this user's watchlist");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setAsset(asset);

        return watchlistRepository.save(watchlist);
    }

    // שליפת כל המטבעות שמשתמש מסוים עוקב אחריהם
    public List<Asset> getUserWatchlist(Long userId) {
        List<Watchlist> watchlistItems = watchlistRepository.findByUserId(userId);

        // המרת רשימת קישורי ה-Watchlist לרשימה נקייה של אובייקטי Asset
        return watchlistItems.stream()
                .map(Watchlist::getAsset)
                .collect(Collectors.toList());
    }

    public void removeAssetFromWatchlist(Long userId, String symbol) {
        // *** תיקון למניעת קריסה: הסרה תתבצע רק אם המניה באמת קיימת במסד הנתונים ***
        Asset asset = assetRepository.findBySymbol(symbol.toUpperCase()).orElse(null);
        
        if (asset != null) {
            watchlistRepository.deleteByUserIdAndAssetId(userId, asset.getId());
        }
    }
}

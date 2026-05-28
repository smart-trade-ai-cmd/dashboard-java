package com.amichaimarcus.investment.controller;

import com.amichaimarcus.investment.model.Asset;
import com.amichaimarcus.investment.model.Watchlist;
import com.amichaimarcus.investment.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "*")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    // נקודת קצה להוספת מטבע לרשימה של משתמש: POST http://localhost:8080/api/watchlist/1/add?symbol=BTC
    @PostMapping("/{userId}/add")
    public Watchlist addToWatchlist(@PathVariable Long userId, @RequestParam String symbol) {
        return watchlistService.addAssetToWatchlist(userId, symbol);
    }

    // נקודת קצה לקבלת רשימת המעקב של משתמש: GET http://localhost:8080/api/watchlist/1
    @GetMapping("/{userId}")
    public List<Asset> getWatchlist(@PathVariable Long userId) {
        return watchlistService.getUserWatchlist(userId);
    }

    @DeleteMapping("/{userId}/remove")
    public org.springframework.http.ResponseEntity<?> removeFromWatchlist(@PathVariable Long userId, @RequestParam String symbol) {
        watchlistService.removeAssetFromWatchlist(userId, symbol);
        return org.springframework.http.ResponseEntity.ok().build();
    }
}
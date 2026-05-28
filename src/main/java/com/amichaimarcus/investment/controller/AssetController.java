package com.amichaimarcus.investment.controller;

import com.amichaimarcus.investment.model.Asset;
import com.amichaimarcus.investment.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    @Autowired
    private AssetService assetService;

    // קבלת כל הנכסים והמחירים שלהם: GET /api/assets
    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    // הוספת נכס חדש למערכת (לשימוש ראשוני או עדכון): POST /api/assets
    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return assetService.saveAsset(asset);
    }
}
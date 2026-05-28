package com.amichaimarcus.investment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id") // <--- זה התיקון הקריטי!
    private Long id;

    @Column(name = "asset_name", nullable = false) // <--- התיקון מחבר את השדה לעמודה הקיימת שלך!
    private String name;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "last_updated")
    private java.time.LocalDateTime lastUpdated;
}
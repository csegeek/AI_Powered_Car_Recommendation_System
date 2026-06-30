package com.car_recommendation_system.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "make", nullable = false, length = 80)
    private String make;

    @Column(name = "model", nullable = false, length = 80)
    private String model;

    @Column(name = "variant", nullable = false, length = 120)
    private String variant;

    @Column(name = "price_lakh", nullable = false, precision = 8, scale = 2)
    private BigDecimal priceLakh;

    @Column(name = "fuel_type", nullable = false, length = 20)
    private String fuelType;

    @Column(name = "body_type", nullable = false, length = 20)
    private String bodyType;

    @Column(name = "transmission", nullable = false, length = 15)
    private String transmission;

    @Column(name = "engine_cc", nullable = false)
    private Integer engineCc;

    @Column(name = "mileage_kmpl", nullable = false, precision = 6, scale = 2)
    private BigDecimal mileageKmpl;

    @Column(name = "safety_rating", nullable = false)
    private Short safetyRating;

    @Column(name = "seating_capacity", nullable = false)
    private Short seatingCapacity;

    @Column(name = "boot_space_litres")
    private Integer bootSpaceLitres;

    @Column(name = "primary_use", nullable = false, length = 20)
    private String primaryUse;

    @Column(name = "key_features")
    private String keyFeatures;

    @Column(name = "user_review_summary")
    private String userReviewSummary;

    @Column(name = "avg_user_rating", precision = 3, scale = 1)
    private BigDecimal avgUserRating;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}

package com.car_recommendation_system.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "recommendation_sessions")
@Getter
@Setter
public class RecommendationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "budget_min_lakh", precision = 8, scale = 2)
    private BigDecimal budgetMinLakh;

    @Column(name = "budget_max_lakh", precision = 8, scale = 2)
    private BigDecimal budgetMaxLakh;

    @Column(name = "fuel_type_pref", length = 20)
    private String fuelTypePref;

    @Column(name = "body_type_pref", length = 20)
    private String bodyTypePref;

    @Column(name = "transmission_pref", length = 15)
    private String transmissionPref;

    @Column(name = "seating_required")
    private Short seatingRequired;

    @Column(name = "primary_use_pref", length = 20)
    private String primaryUsePref;

    @Column(name = "must_have_features")
    private String mustHaveFeatures;

    @Column(name = "additional_notes")
    private String additionalNotes;

    @Column(name = "gemini_prompt")
    private String geminiPrompt;

    @Column(name = "gemini_raw_response")
    private String geminiRawResponse;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}

package com.car_recommendation_system.demo.dto;

import com.car_recommendation_system.demo.model.Recommendation;

import java.util.List;
import java.util.UUID;

public record RecommendationOutcomeDto(
        UUID sessionId,
        List<Recommendation> recommendations) {
}

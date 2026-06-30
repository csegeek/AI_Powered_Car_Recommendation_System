package com.car_recommendation_system.demo.dto;

import java.util.List;

public record AIRecommendationResult(
        String prompt,
        String rawResponse,
        List<GeminiPickDto> picks) {
}

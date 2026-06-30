package com.car_recommendation_system.demo.dto;

public record GeminiPickDto(
        Integer carId,
        Integer rank,
        String reason,
        String tradeOff,
        Double matchScore) {
}

package com.car_recommendation_system.demo.dto;

import java.math.BigDecimal;

public record CarCandidateDto(
        Integer id,
        String make,
        String model,
        String variant,
        BigDecimal priceLakh,
        String fuelType,
        String bodyType,
        String transmission,
        Integer engineCc,
        BigDecimal mileageKmpl,
        Short safetyRating,
        Short seatingCapacity,
        Integer bootSpaceLitres,
        String primaryUse,
        String keyFeatures,
        String userReviewSummary,
        BigDecimal avgUserRating) {
}

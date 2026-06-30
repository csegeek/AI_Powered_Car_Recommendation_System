package com.car_recommendation_system.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IntakeFormDto {

    @NotNull(message = "Minimum budget is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum budget must be positive")
    private BigDecimal budgetMinLakh;

    @NotNull(message = "Maximum budget is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum budget must be positive")
    private BigDecimal budgetMaxLakh;

    private String fuelTypePref;

    private String bodyTypePref;

    private String transmissionPref;

    @Min(value = 2, message = "Seating must be at least 2")
    @Max(value = 9, message = "Seating cannot exceed 9")
    private Short seatingRequired;

    private String primaryUsePref;

    private String mustHaveFeatures;

    private String additionalNotes;
}

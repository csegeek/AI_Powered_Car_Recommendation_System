package com.car_recommendation_system.demo.dto;

import java.util.List;

public final class FormOptions {

    public static final List<String> FUEL_TYPES = List.of("Petrol", "Diesel", "CNG", "Electric", "Hybrid");
    public static final List<String> BODY_TYPES = List.of("Hatchback", "Sedan", "SUV", "MUV");
    public static final List<String> TRANSMISSIONS = List.of("Manual", "Automatic");
    public static final List<String> PRIMARY_USES = List.of("City", "Highway", "Family", "Off-road");

    private FormOptions() {
    }
}

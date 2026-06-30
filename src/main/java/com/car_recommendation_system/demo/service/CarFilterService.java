package com.car_recommendation_system.demo.service;

import com.car_recommendation_system.demo.dto.IntakeFormDto;
import com.car_recommendation_system.demo.model.Car;
import com.car_recommendation_system.demo.repository.CarRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarFilterService {

    private static final int MAX_CANDIDATES = 20;
    private static final int MIN_CANDIDATES_BEFORE_BROADENING = 5;

    private final CarRepository carRepository;

    public CarFilterService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> findCandidates(IntakeFormDto form) {
        Pageable limit = PageRequest.of(0, MAX_CANDIDATES);

        List<Car> candidates = carRepository.findCandidates(
                form.getBudgetMinLakh(),
                form.getBudgetMaxLakh(),
                blankToNull(form.getFuelTypePref()),
                blankToNull(form.getBodyTypePref()),
                blankToNull(form.getTransmissionPref()),
                form.getSeatingRequired(),
                blankToNull(form.getPrimaryUsePref()),
                limit);

        if (candidates.size() < MIN_CANDIDATES_BEFORE_BROADENING) {
            candidates = carRepository.findByBudgetOnly(form.getBudgetMinLakh(), form.getBudgetMaxLakh(), limit);
        }

        return candidates;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}

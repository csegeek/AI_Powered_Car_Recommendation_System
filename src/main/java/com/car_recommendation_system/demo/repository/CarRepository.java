package com.car_recommendation_system.demo.repository;

import com.car_recommendation_system.demo.model.Car;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("""
            SELECT c FROM Car c
            WHERE c.priceLakh BETWEEN :minPrice AND :maxPrice
            AND (:fuelType IS NULL OR c.fuelType = :fuelType)
            AND (:bodyType IS NULL OR c.bodyType = :bodyType)
            AND (:transmission IS NULL OR c.transmission = :transmission)
            AND (:minSeats IS NULL OR c.seatingCapacity >= :minSeats)
            AND (:primaryUse IS NULL OR c.primaryUse = :primaryUse)
            ORDER BY c.avgUserRating DESC NULLS LAST, c.priceLakh ASC
            """)
    List<Car> findCandidates(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("fuelType") String fuelType,
            @Param("bodyType") String bodyType,
            @Param("transmission") String transmission,
            @Param("minSeats") Short minSeats,
            @Param("primaryUse") String primaryUse,
            Pageable pageable);

    @Query("""
            SELECT c FROM Car c
            WHERE c.priceLakh BETWEEN :minPrice AND :maxPrice
            ORDER BY c.avgUserRating DESC NULLS LAST, c.priceLakh ASC
            """)
    List<Car> findByBudgetOnly(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}

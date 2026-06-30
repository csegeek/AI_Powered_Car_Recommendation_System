package com.car_recommendation_system.demo.repository;

import com.car_recommendation_system.demo.model.RecommendationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecommendationSessionRepository extends JpaRepository<RecommendationSession, UUID> {
}

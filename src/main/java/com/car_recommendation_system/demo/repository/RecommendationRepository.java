package com.car_recommendation_system.demo.repository;

import com.car_recommendation_system.demo.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {

    @Query("""
            SELECT r FROM Recommendation r
            JOIN FETCH r.car
            WHERE r.session.id = :sessionId
            ORDER BY r.rankOrder ASC
            """)
    List<Recommendation> findBySessionIdOrderByRank(@Param("sessionId") UUID sessionId);
}

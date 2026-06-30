package com.car_recommendation_system.demo.service;

import com.car_recommendation_system.demo.dto.AIRecommendationResult;
import com.car_recommendation_system.demo.dto.GeminiPickDto;
import com.car_recommendation_system.demo.dto.IntakeFormDto;
import com.car_recommendation_system.demo.dto.RecommendationOutcomeDto;
import com.car_recommendation_system.demo.model.Car;
import com.car_recommendation_system.demo.model.Recommendation;
import com.car_recommendation_system.demo.model.RecommendationSession;
import com.car_recommendation_system.demo.repository.RecommendationRepository;
import com.car_recommendation_system.demo.repository.RecommendationSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final int MAX_RECOMMENDATIONS = 5;

    private final CarFilterService carFilterService;
    private final AIService aiService;
    private final RecommendationSessionRepository sessionRepository;
    private final RecommendationRepository recommendationRepository;

    public RecommendationService(CarFilterService carFilterService,
                                  AIService aiService,
                                  RecommendationSessionRepository sessionRepository,
                                  RecommendationRepository recommendationRepository) {
        this.carFilterService = carFilterService;
        this.aiService = aiService;
        this.sessionRepository = sessionRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional
    public RecommendationOutcomeDto processIntake(IntakeFormDto form, String ipAddress, String userAgent) {
        RecommendationSession session = newSession(form, ipAddress, userAgent);
        sessionRepository.save(session);

        List<Car> candidates = carFilterService.findCandidates(form);
        AIRecommendationResult aiResult = aiService.getRecommendations(candidates, form, MAX_RECOMMENDATIONS);

        session.setGeminiPrompt(aiResult.prompt());
        session.setGeminiRawResponse(aiResult.rawResponse());
        sessionRepository.save(session);

        List<Recommendation> recommendations = toRecommendations(session, candidates, aiResult.picks());
        recommendationRepository.saveAll(recommendations);

        return new RecommendationOutcomeDto(session.getId(), recommendations);
    }

    private RecommendationSession newSession(IntakeFormDto form, String ipAddress, String userAgent) {
        RecommendationSession session = new RecommendationSession();
        session.setBudgetMinLakh(form.getBudgetMinLakh());
        session.setBudgetMaxLakh(form.getBudgetMaxLakh());
        session.setFuelTypePref(form.getFuelTypePref());
        session.setBodyTypePref(form.getBodyTypePref());
        session.setTransmissionPref(form.getTransmissionPref());
        session.setSeatingRequired(form.getSeatingRequired());
        session.setPrimaryUsePref(form.getPrimaryUsePref());
        session.setMustHaveFeatures(form.getMustHaveFeatures());
        session.setAdditionalNotes(form.getAdditionalNotes());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        return session;
    }

    private List<Recommendation> toRecommendations(RecommendationSession session, List<Car> candidates, List<GeminiPickDto> picks) {
        Map<Integer, Car> candidatesById = candidates.stream()
                .collect(Collectors.toMap(Car::getId, Function.identity()));

        List<Recommendation> recommendations = new ArrayList<>();
        Set<Integer> usedCarIds = new HashSet<>();

        List<GeminiPickDto> ordered = picks.stream()
                .sorted(Comparator.comparing(p -> p.rank() == null ? Integer.MAX_VALUE : p.rank()))
                .toList();

        for (GeminiPickDto pick : ordered) {
            if (recommendations.size() >= MAX_RECOMMENDATIONS) {
                break;
            }
            Car car = pick.carId() == null ? null : candidatesById.get(pick.carId());
            if (car == null || !usedCarIds.add(car.getId())) {
                continue;
            }

            Recommendation recommendation = new Recommendation();
            recommendation.setSession(session);
            recommendation.setCar(car);
            recommendation.setRankOrder((short) (recommendations.size() + 1));
            recommendation.setGeminiReason(pick.reason() == null || pick.reason().isBlank()
                    ? "Strong match for your stated budget and preferences."
                    : pick.reason());
            recommendation.setTradeOffNote(pick.tradeOff());
            recommendation.setMatchScore(pick.matchScore() == null ? null : BigDecimal.valueOf(pick.matchScore()));
            recommendations.add(recommendation);
        }

        return recommendations;
    }
}

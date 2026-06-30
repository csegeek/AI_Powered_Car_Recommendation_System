package com.car_recommendation_system.demo.service;

import com.car_recommendation_system.demo.dto.AIRecommendationResult;
import com.car_recommendation_system.demo.dto.CarCandidateDto;
import com.car_recommendation_system.demo.dto.GeminiPickDto;
import com.car_recommendation_system.demo.dto.IntakeFormDto;
import com.car_recommendation_system.demo.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final Resource recommendationPromptResource;

    public AIService(ChatClient chatClient,
                      ObjectMapper objectMapper,
                      @Value("classpath:prompts/car_recommendation_request.st") Resource recommendationPromptResource) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.recommendationPromptResource = recommendationPromptResource;
    }

    public AIRecommendationResult getRecommendations(List<Car> candidates, IntakeFormDto form, int maxRecommendations) {
        String candidatesJson = toCandidatesJson(candidates);
        String prompt = buildPrompt(candidatesJson, form, maxRecommendations);

        String rawResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        List<GeminiPickDto> picks = parsePicks(rawResponse);
        return new AIRecommendationResult(prompt, rawResponse, picks);
    }

    private String buildPrompt(String candidatesJson, IntakeFormDto form, int maxRecommendations) {
        PromptTemplate promptTemplate = new PromptTemplate(recommendationPromptResource);
        Map<String, Object> variables = new HashMap<>();
        variables.put("budgetRange", form.getBudgetMinLakh() + "-" + form.getBudgetMaxLakh());
        variables.put("fuelTypePref", valueOrAny(form.getFuelTypePref()));
        variables.put("bodyTypePref", valueOrAny(form.getBodyTypePref()));
        variables.put("transmissionPref", valueOrAny(form.getTransmissionPref()));
        variables.put("seatingRequired", form.getSeatingRequired() == null ? "Any" : form.getSeatingRequired().toString());
        variables.put("primaryUsePref", valueOrAny(form.getPrimaryUsePref()));
        variables.put("mustHaveFeatures", valueOrAny(form.getMustHaveFeatures()));
        variables.put("additionalNotes", valueOrAny(form.getAdditionalNotes()));
        variables.put("candidates", candidatesJson);
        variables.put("maxRecommendations", maxRecommendations);
        return promptTemplate.render(variables);
    }

    private String valueOrAny(String value) {
        return (value == null || value.isBlank()) ? "No preference" : value;
    }

    private String toCandidatesJson(List<Car> candidates) {
        List<CarCandidateDto> views = candidates.stream().map(this::toCandidateView).toList();
        try {
            return objectMapper.writeValueAsString(views);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize candidate cars for Gemini prompt", e);
        }
    }

    private CarCandidateDto toCandidateView(Car car) {
        return new CarCandidateDto(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getVariant(),
                car.getPriceLakh(),
                car.getFuelType(),
                car.getBodyType(),
                car.getTransmission(),
                car.getEngineCc(),
                car.getMileageKmpl(),
                car.getSafetyRating(),
                car.getSeatingCapacity(),
                car.getBootSpaceLitres(),
                car.getPrimaryUse(),
                car.getKeyFeatures(),
                car.getUserReviewSummary(),
                car.getAvgUserRating());
    }

    private List<GeminiPickDto> parsePicks(String rawResponse) {
        String cleaned = stripMarkdownFences(rawResponse);
        try {
            return objectMapper.readValue(cleaned, objectMapper.getTypeFactory().constructCollectionType(List.class, GeminiPickDto.class));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Gemini response as JSON: " + rawResponse, e);
        }
    }

    private String stripMarkdownFences(String response) {
        String trimmed = response.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline != -1 && lastFence > firstNewline) {
                trimmed = trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}

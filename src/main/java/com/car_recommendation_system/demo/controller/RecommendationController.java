package com.car_recommendation_system.demo.controller;

import com.car_recommendation_system.demo.dto.IntakeFormDto;
import com.car_recommendation_system.demo.dto.RecommendationOutcomeDto;
import com.car_recommendation_system.demo.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public String recommend(@Valid @ModelAttribute("intakeForm") IntakeFormDto form,
                             BindingResult bindingResult,
                             Model model,
                             HttpServletRequest request) {
        if (form.getBudgetMinLakh() != null && form.getBudgetMaxLakh() != null
                && form.getBudgetMinLakh().compareTo(form.getBudgetMaxLakh()) > 0) {
            bindingResult.rejectValue("budgetMaxLakh", "range", "Maximum budget must be greater than or equal to minimum budget");
        }

        if (bindingResult.hasErrors()) {
            HomeController.addFormOptions(model);
            return "index";
        }

        RecommendationOutcomeDto outcome = recommendationService.processIntake(
                form, request.getRemoteAddr(), request.getHeader("User-Agent"));

        model.addAttribute("sessionId", outcome.sessionId());
        model.addAttribute("recommendations", outcome.recommendations());
        return "results";
    }
}

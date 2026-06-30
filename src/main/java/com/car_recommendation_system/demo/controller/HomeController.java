package com.car_recommendation_system.demo.controller;

import com.car_recommendation_system.demo.dto.FormOptions;
import com.car_recommendation_system.demo.dto.IntakeFormDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("intakeForm", new IntakeFormDto());
        addFormOptions(model);
        return "index";
    }

    static void addFormOptions(Model model) {
        model.addAttribute("fuelTypes", FormOptions.FUEL_TYPES);
        model.addAttribute("bodyTypes", FormOptions.BODY_TYPES);
        model.addAttribute("transmissions", FormOptions.TRANSMISSIONS);
        model.addAttribute("primaryUses", FormOptions.PRIMARY_USES);
    }
}

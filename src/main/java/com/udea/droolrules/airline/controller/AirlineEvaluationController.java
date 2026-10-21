package com.udea.droolrules.airline.controller;

import com.udea.droolrules.airline.model.AirlineDecision;
import com.udea.droolrules.airline.model.AirlineRequest;
import com.udea.droolrules.airline.model.Flight;
import com.udea.droolrules.airline.model.Passenger;
import com.udea.droolrules.airline.service.AirlineEvaluationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * API REST y vistas web para la evaluación de reglas de la aerolínea.
 */
@Controller
@RequestMapping("/airline")
public class AirlineEvaluationController {

    @Autowired
    private AirlineEvaluationService evaluationService;

    // Endpoint REST -----------------------------------------------------------
    @PostMapping("/api/evaluate")
    @ResponseBody
    public AirlineDecision evaluateApi(@Valid @RequestBody AirlineRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Errores de validacion");
            AirlineDecision decision = new AirlineDecision();
            decision.setMessage("Error de validacion: " + errorMessage);
            return decision;
        }
        return evaluationService.evaluate(request);
    }

    // Vistas web --------------------------------------------------------------
    @GetMapping("/form")
    public String showForm(Model model) {
        AirlineRequest request = new AirlineRequest();
        request.setPassenger(new Passenger());
        request.setFlight(new Flight());
        model.addAttribute("airlineRequest", request);
        return "airline_form";
    }

    @PostMapping("/evaluate")
    public String evaluateWeb(@Valid AirlineRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "airline_form";
        }
        AirlineDecision decision = evaluationService.evaluate(request);
        model.addAttribute("airlineDecision", decision);
        model.addAttribute("airlineRequest", request);
        return "airline_result";
    }
}

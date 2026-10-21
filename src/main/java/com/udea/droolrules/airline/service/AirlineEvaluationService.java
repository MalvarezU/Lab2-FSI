package com.udea.droolrules.airline.service;

import com.udea.droolrules.airline.model.AirlineDecision;
import com.udea.droolrules.airline.model.AirlineRequest;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que evalúa una solicitud de la aerolínea ejecutando las reglas DRL.
 */
@Service
public class AirlineEvaluationService {

    @Autowired
    private KieContainer kieContainer;

    public AirlineDecision evaluate(AirlineRequest request) {
        AirlineDecision decision = new AirlineDecision();
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(request);
            // El vuelo se inserta como hecho independiente: la regla 5 aplica
            // modify() sobre él para marcar el asiento de emergencia como ocupado.
            kieSession.insert(request.getFlight());
            kieSession.insert(decision);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
        return decision;
    }
}

package com.udea.droolrules.airline;

import com.udea.droolrules.airline.model.AirlineDecision;
import com.udea.droolrules.airline.model.AirlineRequest;
import com.udea.droolrules.airline.model.Flight;
import com.udea.droolrules.airline.model.Passenger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de las 10 reglas de la aerolineaa definidas en
 * rules/airline_rules.drl. Construyen el motor de reglas directamente desde el
 * DRL, sin necesidad del contexto de Spring.
 */
class AirlineRulesTest {

    private static KieContainer kieContainer;

    @BeforeAll
    static void setUp() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/airline_rules.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

    private AirlineDecision evaluate(AirlineRequest request) {
        AirlineDecision decision = new AirlineDecision();
        KieSession session = kieContainer.newKieSession();
        try {
            session.insert(request);
            session.insert(request.getFlight());
            session.insert(decision);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
        return decision;
    }

    private AirlineRequest request(Passenger passenger, Flight flight, double luggage, String seatPreference) {
        return new AirlineRequest(passenger, flight, luggage, seatPreference, 500.0);
    }

    @Test
    @DisplayName("Regla 1: ascenso a ejecutiva para Gold con retraso > 60 min")
    void rule1_upgradeForGoldWithDelay() {
        Passenger passenger = new Passenger("Ana", 35, "Gold", false);
        Flight flight = new Flight("AV101", 90, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Window"));

        assertTrue(decision.isUpgradedToBusiness());
        assertTrue(decision.getAppliedRules().contains("UpgradeToBusinessClassForFrequentFlyersWithDelays"));
    }

    @Test
    @DisplayName("Regla 1: ascenso a ejecutiva para Platinum con retraso > 60 min")
    void rule1_upgradeForPlatinumWithDelay() {
        Passenger passenger = new Passenger("Beto", 40, "Platinum", false);
        Flight flight = new Flight("AV102", 120, 2.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 10.0, "Aisle"));

        assertTrue(decision.isUpgradedToBusiness());
    }

    @Test
    @DisplayName("Regla 1: no hay ascenso si el retraso es <= 60 min")
    void rule1_noUpgradeWithoutDelay() {
        Passenger passenger = new Passenger("Carla", 30, "Gold", false);
        Flight flight = new Flight("AV103", 60, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Window"));

        assertFalse(decision.isUpgradedToBusiness());
    }

    @Test
    @DisplayName("Regla 2: check-in prioritario para mayores de 65")
    void rule2_priorityCheckInForSeniors() {
        Passenger passenger = new Passenger("Dora", 70, "Basic", false);
        Flight flight = new Flight("AV104", 0, 1.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 5.0, "Any"));

        assertTrue(decision.isPriorityCheckIn());
        assertTrue(decision.getAppliedRules().contains("PriorityCheckInForSeniors"));
    }

    @Test
    @DisplayName("Regla 3: descuento 10% para equipaje menor a 10 kg")
    void rule3_discountForLightLuggage() {
        Passenger passenger = new Passenger("Eli", 28, "Basic", false);
        Flight flight = new Flight("AV105", 0, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 8.0, "Any"));

        assertEquals(10.0, decision.getDiscountPercent());
        assertTrue(decision.getAppliedRules().contains("DiscountForLightLuggage"));
    }

    @Test
    @DisplayName("Regla 4: equipaje > 23 kg niega el ascenso (incluso Gold con retraso)")
    void rule4_denyUpgradeForOverweightLuggage() {
        Passenger passenger = new Passenger("Fran", 45, "Gold", false);
        Flight flight = new Flight("AV106", 90, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 30.0, "Any"));

        assertFalse(decision.isUpgradeEligible());
        assertFalse(decision.isUpgradedToBusiness());
        assertTrue(decision.getAppliedRules().contains("DenyUpgradeForOverweightLuggage"));
    }

    @Test
    @DisplayName("Regla 5: asiento de emergencia para joven 18-40 con preferencia Any")
    void rule5_assignEmergencyExitSeat() {
        Passenger passenger = new Passenger("Gus", 25, "Basic", false);
        Flight flight = new Flight("AV107", 0, 3.0, 2);
        AirlineDecision decision = evaluate(request(passenger, flight, 10.0, "Any"));

        assertTrue(decision.isEmergencyExitSeatAssigned());
        assertEquals(1, flight.getEmergencyExitSeatsAvailable());
        assertTrue(decision.getAppliedRules().contains("AssignEmergencyExitSeatToYoungAdults"));
    }

    @Test
    @DisplayName("Regla 5: no asigna asiento de emergencia si no hay disponibles")
    void rule5_noEmergencySeatWhenUnavailable() {
        Passenger passenger = new Passenger("Hana", 30, "Basic", false);
        Flight flight = new Flight("AV108", 0, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 10.0, "Any"));

        assertFalse(decision.isEmergencyExitSeatAssigned());
    }

    @Test
    @DisplayName("Regla 6: compensacion de $200 por retraso > 180 min")
    void rule6_compensationForExtremeDelays() {
        Passenger passenger = new Passenger("Ivan", 50, "Silver", false);
        Flight flight = new Flight("AV109", 200, 4.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Aisle"));

        assertEquals(200.0, decision.getCompensationAmount());
        assertTrue(decision.getAppliedRules().contains("CompensationForExtremeDelays"));
    }

    @Test
    @DisplayName("Regla 7: 500 puntos de lealtad para membresia != Basic en vuelo > 5 h")
    void rule7_loyaltyPointsForLongFlights() {
        Passenger passenger = new Passenger("Julia", 33, "Silver", false);
        Flight flight = new Flight("AV110", 0, 6.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Window"));

        assertEquals(500, decision.getLoyaltyPoints());
        assertTrue(decision.getAppliedRules().contains("ExtraLoyaltyPointsForLongFlights"));
    }

    @Test
    @DisplayName("Regla 7: Basic no recibe puntos extra")
    void rule7_basicGetsNoPoints() {
        Passenger passenger = new Passenger("Kai", 30, "Basic", false);
        Flight flight = new Flight("AV111", 0, 6.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Window"));

        assertEquals(0, decision.getLoyaltyPoints());
    }

    @Test
    @DisplayName("Regla 8: equipaje > 15 kg no permitido en vuelo < 2 h")
    void rule8_restrictLuggageOnShortFlights() {
        Passenger passenger = new Passenger("Luna", 27, "Basic", false);
        Flight flight = new Flight("AV112", 0, 1.5, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 20.0, "Any"));

        assertFalse(decision.isLuggageAllowed());
        assertTrue(decision.getAppliedRules().contains("RestrictLuggageOnShortFlights"));
    }

    @Test
    @DisplayName("Regla 9: acceso a salon VIP para Platinum")
    void rule9_vipLoungeForPlatinum() {
        Passenger passenger = new Passenger("Mara", 48, "Platinum", false);
        Flight flight = new Flight("AV113", 0, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 10.0, "Window"));

        assertTrue(decision.isVipLoungeAccess());
        assertTrue(decision.getAppliedRules().contains("VipLoungeAccessForPlatinumMembers"));
    }

    @Test
    @DisplayName("Regla 10: asiento preferencial para familias sin preferencia")
    void rule10_preferentialSeatForFamilies() {
        Passenger passenger = new Passenger("Nico", 38, "Basic", true);
        Flight flight = new Flight("AV114", 0, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Any"));

        assertTrue(decision.isPreferentialFamilySeat());
        assertTrue(decision.getAppliedRules().contains("PreferentialSeatForFamilies"));
    }

    @Test
    @DisplayName("Regla 10: familia con preferencia especifica no recibe asiento preferencial")
    void rule10_familyWithSpecificPreferenceGetsNoSeat() {
        Passenger passenger = new Passenger("Olga", 38, "Basic", true);
        Flight flight = new Flight("AV115", 0, 3.0, 0);
        AirlineDecision decision = evaluate(request(passenger, flight, 12.0, "Window"));

        assertFalse(decision.isPreferentialFamilySeat());
    }

    @Test
    @DisplayName("Caso combinado: Platinum de 70 años con retraso extremo acumula varios beneficios")
    void combinedCase_platinumExtremeDelay() {
        Passenger passenger = new Passenger("Pablo", 70, "Platinum", false);
        Flight flight = new Flight("AV116", 200, 6.0, 1);
        AirlineDecision decision = evaluate(request(passenger, flight, 8.0, "Any"));

        assertTrue(decision.isUpgradedToBusiness());            // Regla 1
        assertTrue(decision.isPriorityCheckIn());               // Regla 2
        assertTrue(decision.isVipLoungeAccess());               // Regla 9
        assertEquals(200.0, decision.getCompensationAmount());  // Regla 6
        assertEquals(500, decision.getLoyaltyPoints());         // Regla 7
        assertEquals(10.0, decision.getDiscountPercent());      // Regla 3
        assertFalse(decision.isEmergencyExitSeatAssigned());    // Regla 5 no aplica (edad 70)
    }

    @Test
    @DisplayName("Caso combinado: joven Basic con asiento de emergencia disponible")
    void combinedCase_youngAdultEmergencySeat() {
        Passenger passenger = new Passenger("Rosa", 30, "Basic", false);
        Flight flight = new Flight("AV117", 0, 3.0, 3);
        AirlineDecision decision = evaluate(request(passenger, flight, 10.0, "Any"));

        assertTrue(decision.isEmergencyExitSeatAssigned());     // Regla 5
        assertEquals(2, flight.getEmergencyExitSeatsAvailable());
    }
}

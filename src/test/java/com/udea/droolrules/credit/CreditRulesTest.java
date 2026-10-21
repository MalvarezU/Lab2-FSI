package com.udea.droolrules.credit;

import com.udea.droolrules.model.CreditRequest;
import com.udea.droolrules.model.CreditResponse;
import com.udea.droolrules.model.Customer;
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
 * Pruebas unitarias de las 6 reglas de credito definidas en
 * rules/credit_rules.drl. Los casos replican las 9 pruebas en Postman de la guia.
 */
class CreditRulesTest {

    private static KieContainer kieContainer;

    @BeforeAll
    static void setUp() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/credit_rules.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

    private CreditResponse evaluate(CreditRequest request) {
        CreditResponse response = new CreditResponse();
        KieSession session = kieContainer.newKieSession();
        try {
            session.insert(request);
            session.insert(response);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
        return response;
    }

    private CreditRequest request(int creditScore, double monthlyIncome, String creditType, double amount) {
        Customer customer = new Customer("Cliente", 30, monthlyIncome, creditScore);
        return new CreditRequest(customer, creditType, amount);
    }

    @Test
    @DisplayName("Prueba 1: RejectLowCreditScore (puntaje 450)")
    void test1_rejectLowCreditScore() {
        CreditResponse response = evaluate(request(450, 2000.0, "personal", 10000.0));

        assertFalse(response.isApproved());
        assertEquals("Solicitud rechazada: Puntaje crediticio demasiado bajo.", response.getMessage());
    }

    @Test
    @DisplayName("Prueba 3: PersonalCreditApproval tasa baja (750)")
    void test3_personalLowRate() {
        CreditResponse response = evaluate(request(750, 2000.0, "personal", 15000.0));

        assertTrue(response.isApproved());
        assertEquals(15000.0, response.getApprovedAmount());
        assertEquals(5.0, response.getInterestRate());
    }

    @Test
    @DisplayName("Prueba 4: PersonalCreditApproval tasa alta (600) con monto limitado")
    void test4_personalHighRate() {
        CreditResponse response = evaluate(request(600, 1500.0, "personal", 20000.0));

        assertTrue(response.isApproved());
        assertEquals(15000.0, response.getApprovedAmount());
        assertEquals(8.0, response.getInterestRate());
    }

    @Test
    @DisplayName("Prueba 5: MortgageCreditApproval tasa baja (800)")
    void test5_mortgageLowRate() {
        CreditResponse response = evaluate(request(800, 4000.0, "hipotecario", 150000.0));

        assertTrue(response.isApproved());
        assertEquals(150000.0, response.getApprovedAmount());
        assertEquals(3.5, response.getInterestRate());
    }

    @Test
    @DisplayName("Prueba 6: MortgageCreditApproval tasa alta (650) con monto limitado")
    void test6_mortgageHighRate() {
        CreditResponse response = evaluate(request(650, 3500.0, "hipotecario", 200000.0));

        assertTrue(response.isApproved());
        assertEquals(175000.0, response.getApprovedAmount());
        assertEquals(4.5, response.getInterestRate());
    }

    @Test
    @DisplayName("Prueba 7: AutoCreditApproval tasa baja (720)")
    void test7_autoLowRate() {
        CreditResponse response = evaluate(request(720, 2500.0, "automotriz", 30000.0));

        assertTrue(response.isApproved());
        assertEquals(30000.0, response.getApprovedAmount());
        assertEquals(6.0, response.getInterestRate());
    }

    @Test
    @DisplayName("Prueba 8: AutoCreditApproval tasa alta (600) con monto limitado")
    void test8_autoHighRate() {
        CreditResponse response = evaluate(request(600, 1800.0, "automotriz", 40000.0));

        assertTrue(response.isApproved());
        assertEquals(36000.0, response.getApprovedAmount());
        assertEquals(7.5, response.getInterestRate());
    }

    @Test
    @DisplayName("Prueba 9: RejectInvalidCreditType (hipotecario sin ingresos suficientes)")
    void test9_rejectInvalidCreditType() {
        CreditResponse response = evaluate(request(650, 2000.0, "hipotecario", 100000.0));

        assertFalse(response.isApproved());
        assertEquals("Solicitud rechazada: No cumple con los requisitos para el tipo de crédito solicitado.", response.getMessage());
    }
}

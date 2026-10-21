package com.udea.droolrules.credit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba del endpoint REST de creditos. La guia documenta que la validacion de
 * Spring intercepta la edad menor a 18 ANTES de que Drools evalúe la regla
 * "RejectUnderAge"; por eso este caso se valida a nivel de API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CreditValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Prueba 2 (API): edad 16 es rechazada por validacion de Spring")
    void ageBelow18IsRejectedByValidation() throws Exception {
        String body = """
                {
                  "customer": {
                    "name": "Luis Martinez",
                    "age": 16,
                    "monthlyIncome": 2000.0,
                    "creditScore": 700
                  },
                  "creditType": "personal",
                  "requestAmount": 10000.0
                }
                """;

        mockMvc.perform(post("/credit/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(false))
                .andExpect(jsonPath("$.message").value("Error de validacion: La edad debe ser de almenos 18 años"));
    }

    @Test
    @DisplayName("API: solicitud valida de credito personal aprobada")
    void validPersonalCreditIsApproved() throws Exception {
        String body = """
                {
                  "customer": {
                    "name": "Juan Perez",
                    "age": 30,
                    "monthlyIncome": 2000.0,
                    "creditScore": 750
                  },
                  "creditType": "personal",
                  "requestAmount": 15000.0
                }
                """;

        mockMvc.perform(post("/credit/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(true))
                .andExpect(jsonPath("$.approvedAmount").value(15000.0))
                .andExpect(jsonPath("$.interestRate").value(5.0));
    }
}

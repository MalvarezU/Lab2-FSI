package com.udea.droolrules.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Capa de IA: explica en lenguaje natural la decisión calculada por Drools.
 * Solo se crea cuando la propiedad app.ai.enabled=true y se provee una API key
 * valida en la variable de entorno OPENAI_API_KEY.
 */
@Service
@ConditionalOnProperty(name = "app.ai.enabled", havingValue = "true")
public class AIExplanationService {

    private final ChatClient chatClient;

    public AIExplanationService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String explain(String decision) {
        String prompt = """
                Eres un experto en reglas bancarias.
                Explica el siguiente resultado generado por Drools.
                Resultado:
                %s
                Explica:
                - Que regla se activo
                - Por que se activo
                - Que significa para el cliente
                """.formatted(decision);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}

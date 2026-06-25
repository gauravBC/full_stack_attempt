package com.nurtureai.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiClientConfig {

    @Bean
    MealPlanAiClient mealPlanAiClient(AiProperties properties, RestClient.Builder restClientBuilder) {
        if (properties.openAiEnabled()) {
            return new OpenAiMealPlanClient(properties, restClientBuilder.build());
        }

        if (properties.openAiRequested()) {
            throw new IllegalStateException("AI_PROVIDER=openai requires OPENAI_API_KEY to be set on the backend.");
        }

        if (properties.ollamaEnabled()) {
            return new OllamaMealPlanClient(properties, restClientBuilder.build());
        }

        return new MockMealPlanAiClient();
    }
}

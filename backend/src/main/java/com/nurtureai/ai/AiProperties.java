package com.nurtureai.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nurtureai.ai")
public record AiProperties(
    String provider,
    String apiKey,
    String model,
    String responsesUrl,
    String ollamaUrl,
    String ollamaModel,
    int timeoutSeconds
) {

    public boolean openAiRequested() {
        return "openai".equalsIgnoreCase(provider);
    }

    public boolean openAiEnabled() {
        return openAiRequested() && apiKey != null && !apiKey.isBlank();
    }

    public boolean ollamaEnabled() {
        return "ollama".equalsIgnoreCase(provider);
    }

    public String activeProvider() {
        if (openAiEnabled()) {
            return "openai";
        }
        if (ollamaEnabled()) {
            return "ollama";
        }
        return "mock";
    }

    public String activeModel() {
        if (ollamaEnabled()) {
            return ollamaModel();
        }
        return model();
    }
}

package com.nurtureai.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nurtureai.cors")
public record CorsProperties(List<String> allowedOrigins) {
}

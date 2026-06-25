package com.nurtureai.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nurtureai.datasource")
public record ReadReplicaProperties(List<String> readReplicas) {
}

package com.nurtureai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NurtureAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NurtureAiApplication.class, args);
    }
}

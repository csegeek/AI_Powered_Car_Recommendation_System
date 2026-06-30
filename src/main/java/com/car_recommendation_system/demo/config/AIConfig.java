package com.car_recommendation_system.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                                  @Value("classpath:prompts/car_consultant.st") Resource systemPrompt) throws IOException {
        String systemPromptText = StreamUtils.copyToString(systemPrompt.getInputStream(), StandardCharsets.UTF_8);
        return chatClientBuilder
                .defaultSystem(systemPromptText)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

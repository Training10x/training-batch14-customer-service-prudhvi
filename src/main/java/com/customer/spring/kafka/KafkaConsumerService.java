package com.customer.spring.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;


import java.util.Map;

@Service
public class KafkaConsumerService {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "candidate-to-customer", groupId = "customer-service")
    public void listen(String message) {
        try {
            Map<String, Object> candidateMap = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});
            logger.info("Received Candidate: {}", candidateMap);
        } catch (JsonProcessingException e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
    }


}

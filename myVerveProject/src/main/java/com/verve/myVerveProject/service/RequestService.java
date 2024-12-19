package com.verve.myVerveProject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
public class RequestService {

  @Autowired
  private RedisTemplate redisTemplate;
//  private final RedisTemplate<String, Integer> redisTemplate;
  private final Logger logger = LoggerFactory.getLogger(RequestService.class);
  private final WebClient webClient = WebClient.create();
  private final ExecutorService executor = Executors.newFixedThreadPool(200);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ConcurrentHashMap<Integer, Boolean> requestMap = new ConcurrentHashMap<>();

  public RequestService(RedisTemplate<String, String> redisTemplate, KafkaTemplate<String, String> kafkaTemplate) {
    this.redisTemplate = redisTemplate;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void processRequest(int id, String endpoint) {
    // Add to Redis for deduplication
    Boolean isNew = redisTemplate.opsForValue().setIfAbsent(String.valueOf(id), 1, Duration.ofMinutes(1));
    if (isNew) {
      requestMap.put(id, true);
    }

    logger.info("Processed request with Id : {}, endpoint: {} into redis", id, endpoint);

    // Handle optional endpoint
    if (endpoint != null) {
      sendHttpPostRequest(endpoint);
    }
  }

  private void sendHttpPostRequest(String endpoint) {
    int count = requestMap.size();
    try {
      Map<String, Object> payload = new HashMap<>();
      payload.put("uniqueRequestCount", count);

      webClient.post()
          .uri(endpoint)
          .bodyValue(payload)
          .retrieve()
          .toBodilessEntity()
          .subscribe(response -> logger.info("HTTP POST to {} succeeded with status {}", endpoint, response.getStatusCode()));
    } catch (Exception e) {
      logger.error("Error sending HTTP POST request to {}: {}", endpoint, e.getMessage());
    }
  }

  @Scheduled(fixedRate = 60000)
  public void logAndResetCounts() {
    int uniqueCount = requestMap.size();
    logger.info("Unique request count in last minute: {}", uniqueCount);

    String message = "Unique requests in the last minute: " + uniqueCount;
    executor.submit(() -> kafkaTemplate.send(TOPIC, message));
    logger.info("Sent message to Kafka topic {}: {}", TOPIC, message);

    requestMap.clear();
  }
}

package com.verve.myVerveProject.ServiceTest;

import com.verve.myVerveProject.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestServiceTest {

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ValueOperations<String, Object> valueOperations;

  @Mock
  private WebClient webClient;

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @InjectMocks
  private RequestService requestService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testProcessRequest_NewRequest() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(anyString(), any(), any())).thenReturn(true);
    requestService.processRequest(1, null);
    verify(valueOperations, times(1)).setIfAbsent(anyString(), any(), any());
  }

  @Test
  void testProcessRequest_ExistingRequest() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(anyString(), any(), any())).thenReturn(false);
    requestService.processRequest(1, null);
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).setIfAbsent(anyString(), any(), any());
  }
}

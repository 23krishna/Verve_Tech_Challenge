package com.verve.myVerveProject.ControllerTest;

import com.verve.myVerveProject.controller.VerveController;
import com.verve.myVerveProject.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class VerveControllerTest {
  private RequestService requestService;
  private VerveController verveController;

  @BeforeEach
  void setup() {
    requestService = Mockito.mock(RequestService.class);
    verveController = new VerveController(requestService);
  }

  @Test
  void testAcceptRequest_Success() {
    doNothing().when(requestService).processRequest(anyInt(), anyString());
    ResponseEntity<String> response = verveController.acceptRequest(1, "http://example.com");
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("ok", response.getBody());
  }

  @Test
  void testAcceptRequest_Failure() {
    doThrow(new RuntimeException("Error")).when(requestService).processRequest(anyInt(), anyString());
    ResponseEntity<String> response = verveController.acceptRequest(1, "http://example.com");
    assertEquals(500, response.getStatusCodeValue());
    assertEquals("failed", response.getBody());
  }
}

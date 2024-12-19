package com.verve.myVerveProject.controller;

import com.verve.myVerveProject.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verve")
public class VerveController {
  private final RequestService requestService;
  private final Logger logger = LoggerFactory.getLogger(VerveController.class);

  public VerveController(RequestService requestService) {
    this.requestService = requestService;
  }

  @GetMapping("/accept")
  public ResponseEntity<String> acceptRequest(
      @RequestParam int id,
      @RequestParam(required = false) String endpoint) {
    try {
      requestService.processRequest(id, endpoint);
      return ResponseEntity.ok("ok");
    } catch (Exception e) {
      logger.error("Failed - {}", id);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
    }
  }
}

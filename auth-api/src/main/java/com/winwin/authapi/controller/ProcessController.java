package com.winwin.authapi.controller;

/**
 * Orchestration Controller for Business Logic.
 * * Manages the "Process" workflow by validating user context, delegating data
 * transformation to the internal Data API via RestTemplate, and persisting
 * the transaction results to the PostgreSQL log.
 */

import com.winwin.authapi.model.ProcessingLog;
import com.winwin.authapi.model.User;
import com.winwin.authapi.repository.ProcessingLogRepository;
import com.winwin.authapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessController {

    private final UserRepository userRepository;
    private final ProcessingLogRepository logRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.service-b.url}")
    private String serviceBUrl;

    @Value("${app.service-b.token}")
    private String internalToken;

    @PostMapping
    public ResponseEntity<?> process(@RequestBody TextRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Token", internalToken);
        HttpEntity<TextRequest> entity = new HttpEntity<>(req, headers);

        try {
            ResponseEntity<TextResponse> response = restTemplate.postForEntity(serviceBUrl, entity, TextResponse.class);
            String result = response.getBody() != null ? response.getBody().getResult() : "";

            ProcessingLog log = ProcessingLog.builder()
                    .userId(user.getId())
                    .inputText(req.getText())
                    .outputText(result)
                    .createdAt(LocalDateTime.now())
                    .build();
            logRepository.save(log);

            return ResponseEntity.ok(new TextResponse(result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @Data
    public static class TextRequest {
        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TextResponse {
        private String result;
    }
}
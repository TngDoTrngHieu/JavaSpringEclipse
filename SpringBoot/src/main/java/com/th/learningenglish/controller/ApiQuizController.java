package com.th.learningenglish.controller;

import java.util.Map;

import com.th.learningenglish.service.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiQuizController {

    @Autowired
    private GeminiService geminiService;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping(path = "/api/ai/generate-quiz", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateQuiz(@RequestBody Map<String, Object> body) {
        Object passageObj = body.get("passage");
        if (passageObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "'passage' field is required"));
        }

        String passage = String.valueOf(passageObj);
        try {
            String aiResult = geminiService.generateQuizFromPassage(passage);

            // Try parse to JSON and return as structured JSON
            try {
                JsonNode node = mapper.readTree(aiResult);
                return ResponseEntity.ok(mapper.convertValue(node, Object.class));
            } catch (Exception parseEx) {
                // If parsing fails, return the raw text so admin can inspect
                return ResponseEntity.ok(Map.of("raw", aiResult));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

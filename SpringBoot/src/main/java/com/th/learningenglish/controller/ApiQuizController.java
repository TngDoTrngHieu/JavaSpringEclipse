package com.th.learningenglish.controller;

import java.security.Principal;
import java.util.Map;

import com.th.learningenglish.service.GeminiService;
import com.th.learningenglish.service.UserService;
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

    @Autowired
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping(path = "/api/ai/generate-quiz", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateQuiz(@RequestBody Map<String, Object> body, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Bạn cần đăng nhập để tạo bộ câu hỏi AI."));
        }
        if (!userService.isUserVip(principal.getName())) {
            return ResponseEntity.status(403).body(Map.of("error", "Tính năng tạo câu hỏi AI hiện chỉ dành cho thành viên VIP."));
        }

        Object passageObj = body.get("passage");
        if (passageObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu nội dung đầu vào: vui lòng gửi trường 'passage'."));
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
            return ResponseEntity.status(500).body(Map.of("error", "Hệ thống AI đang bận. Vui lòng thử lại sau ít phút."));
        }
    }
}

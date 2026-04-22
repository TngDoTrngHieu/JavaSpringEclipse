package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.th.learningenglish.dto.ReadingSubmitDTO;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.pojo.PracticeSessions;
import com.th.learningenglish.pojo.ProgressTrackers;
import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.pojo.UserAnswers;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.service.LessonsService;
import com.th.learningenglish.service.PracticeSessionService;
import com.th.learningenglish.service.ProgressTrackerService;
import com.th.learningenglish.service.SectionService;
import com.th.learningenglish.service.UserService;
import com.th.learningenglish.service.UserAnswerService;

@RestController
@RequestMapping("/api/reading")
public class ApiReadingController {

    @Autowired
    private LessonsService lessonsService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PracticeSessionService practiceSessionService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private ProgressTrackerService progressTrackerService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody ReadingSubmitDTO dto, Principal principal) {
        try {
            if (dto == null || dto.getLessonId() == null || dto.getLessonId() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "lessonId is required"));
            }

            Map<String, String> answers = dto.getAnswers();
            Lessons lesson = lessonsService.getLessonById(dto.getLessonId());
            Users user = userService.getUserByUsername(principal.getName());
            List<Sections> sections = sectionService.getSectionsByLesson(dto.getLessonId());

            int score = 0;
            int total = 0;

            Map<String, String> correctAnswers = new HashMap<>();
            Map<Long, String> rawCorrectAnswersBySectionId = new HashMap<>();

            for (Sections s : sections) {
                if (s == null || s.getId() == null || s.getCorrectAnswer() == null) {
                    continue;
                }
                // Skip passage section
                if (s.getSectionType() != null && "READING_PASSAGE".equals(s.getSectionType().getName())) {
                    continue;
                }

                String rawCorrect = extractCorrectAnswerValue(s.getCorrectAnswer());
                String expected = normalizeAnswer(rawCorrect);
                if (expected == null) {
                    continue;
                }

                correctAnswers.put(String.valueOf(s.getId()), rawCorrect == null ? null : rawCorrect);
                rawCorrectAnswersBySectionId.put(s.getId(), rawCorrect);

                total++;
                String userAnswer = answers != null ? normalizeAnswer(answers.get(String.valueOf(s.getId()))) : null;
                if (expected.equals(userAnswer)) {
                    score++;
                }
            }

            BigDecimal finalScore = BigDecimal.valueOf(score);
            PracticeSessions session = practiceSessionService.recordSession(
                    user.getId(),
                    lesson.getId(),
                    finalScore,
                    0,
                    "Reading score: " + score + "/" + total);

            for (Sections section : sections) {
                if (section == null || section.getId() == null || !rawCorrectAnswersBySectionId.containsKey(section.getId())) {
                    continue;
                }

                String userRawAnswer = answers != null ? answers.get(String.valueOf(section.getId())) : null;
                String expected = normalizeAnswer(rawCorrectAnswersBySectionId.get(section.getId()));
                String actual = normalizeAnswer(userRawAnswer);

                UserAnswers userAnswer = new UserAnswers();
                userAnswer.setSession(session);
                userAnswer.setSection(section);
                userAnswer.setAnswer(userRawAnswer);
                userAnswer.setIsCorrect(Objects.equals(expected, actual));
                userAnswer.setScore(Objects.equals(expected, actual) ? BigDecimal.ONE : BigDecimal.ZERO);
                userAnswerService.create(userAnswer);
            }

            progressTrackerService.updateProgress(user.getId(), ProgressTrackers.Skill.READING, finalScore);

            return ResponseEntity.ok(Map.of(
                    "score", score,
                    "total", total,
                    "correctAnswers", correctAnswers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private static String normalizeAnswer(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }

    private static String extractCorrectAnswerValue(String raw) {
        String trimmed = raw == null ? null : raw.trim();
        if (trimmed == null || trimmed.isEmpty()) {
            return null;
        }
        try {
            JsonNode node = MAPPER.readTree(trimmed);
            if (node.isObject()) {
                if (node.has("value")) {
                    return node.path("value").asText(null);
                }
                if (node.has("text")) {
                    return node.path("text").asText(null);
                }
                if (node.has("answer")) {
                    return node.path("answer").asText(null);
                }
                if (node.size() == 1) {
                    return node.elements().next().asText(null);
                }
            }
            if (node.isTextual()) {
                return node.asText();
            }
            if (node.isArray() && node.size() > 0) {
                return node.get(0).asText(null);
            }
        } catch (Exception ignored) {
            // fallback: plain text
        }
        return trimmed;
    }
}

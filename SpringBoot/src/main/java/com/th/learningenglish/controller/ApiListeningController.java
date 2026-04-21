package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.th.learningenglish.dto.ListeningSubmitDTO;
import com.th.learningenglish.dto.ResultDTO;
import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.repository.LessonRepository;
import com.th.learningenglish.repository.SectionRepository;

@RestController
@RequestMapping("/api/listening")
public class ApiListeningController {

	@Autowired
	private LessonRepository lessonRepository;

	@Autowired
	private SectionRepository sectionRepository;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@PostMapping("/submit")
	public ResponseEntity<?> submit(@RequestBody ListeningSubmitDTO dto) {
		try {
			if (dto == null || dto.getLessonId() == null || dto.getLessonId() <= 0) {
				return ResponseEntity.badRequest().body(Map.of("error", "lessonId is required"));
			}
			if (!lessonRepository.existsById(dto.getLessonId())) {
				return ResponseEntity.badRequest().body(Map.of("error", "Lesson not found"));
			}

			Map<String, String> answers = dto.getAnswers();
			List<Sections> sections = sectionRepository.findByLesson_IdOrderByPositionAsc(dto.getLessonId());

			int score = 0;
			int total = 0;

			// Map for frontend to show correct answers: sectionId (string) -> raw correct
			// answer
			Map<String, String> correctAnswers = new HashMap<>();

			for (Sections s : sections) {
				if (s == null || s.getId() == null || s.getCorrectAnswer() == null) {
					continue;
				}
				String rawCorrect = extractCorrectAnswerValue(s.getCorrectAnswer());
				String expected = normalizeAnswer(rawCorrect);
				if (expected == null) {
					continue;
				}

				// store raw value for frontend display/highlight
				correctAnswers.put(String.valueOf(s.getId()), rawCorrect == null ? null : rawCorrect);

				total++;
				String userAnswer = answers != null ? normalizeAnswer(answers.get(String.valueOf(s.getId()))) : null;
				if (Objects.equals(expected, userAnswer)) {
					score++;
				}
			}

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
				// Single-key object: { "A": "..." } or arbitrary wrapper
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
			// Fallback: correctAnswer is plain text in DB.
		}
		return trimmed;
	}
}

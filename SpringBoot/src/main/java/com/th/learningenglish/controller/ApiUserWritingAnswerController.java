package com.th.learningenglish.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.pojo.ProgressTrackers;
import com.th.learningenglish.pojo.UserWritingAnswers;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.service.GeminiService;
import com.th.learningenglish.service.LessonsService;
import com.th.learningenglish.service.PracticeSessionService;
import com.th.learningenglish.service.ProgressTrackerService;
import com.th.learningenglish.service.UserService;
import com.th.learningenglish.service.UserWritingAnswerService;

@RestController
@RequestMapping("/api/user-writing-answers")
public class ApiUserWritingAnswerController {
	private final ProgressTrackerService progressTrackerService;

	private final PracticeSessionService practiceSessionService;

	@Autowired
	private UserWritingAnswerService userWritingAnswerService;

	@Autowired
	private GeminiService geminiService;

	@Autowired
	private UserService userService;

	@Autowired
	private LessonsService lessonsService;

	ApiUserWritingAnswerController(PracticeSessionService practiceSessionService,
			ProgressTrackerService progressTrackerService) {
		this.practiceSessionService = practiceSessionService;
		this.progressTrackerService = progressTrackerService;
	}

	@GetMapping
	public List<UserWritingAnswers> getAll() {
		return userWritingAnswerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserWritingAnswers> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(userWritingAnswerService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public UserWritingAnswers create(@RequestBody UserWritingAnswers item) {
		return userWritingAnswerService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserWritingAnswers> update(@PathVariable Long id, @RequestBody UserWritingAnswers payload) {
		try {
			return ResponseEntity.ok(userWritingAnswerService.update(id, payload));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try {
			userWritingAnswerService.findById(id);
			userWritingAnswerService.delete(id);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/submit")
	public ResponseEntity<?> submit(@RequestBody Map<String, Object> req, Principal principal) {
		try {
			String essay = (String) req.get("content");
			Long lessonId = Long.parseLong(req.get("lessonId").toString());
			int duration = req.containsKey("durationSeconds") ? Integer.parseInt(req.get("durationSeconds").toString())
					: 0;

			// Fetch lesson TRƯỚC rồi mới dùng
			Lessons lesson = lessonsService.getLessonByIdWithLessonType(lessonId);

			// Giờ mới detect taskType
			String lessonTypeName = lesson.getLessonType().getName().toLowerCase();
			String taskType = lessonTypeName.contains("task 1") ? "task1" : "task2";

			// Gọi đúng method theo taskType
			String jsonResult;
			if ("task1".equals(taskType) && lesson.getImageUrl() != null) {
				jsonResult = geminiService.evaluateTask1Essay(essay, lesson.getImageUrl());
			} else {
				jsonResult = geminiService.evaluateTask2Essay(essay);
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(jsonResult);

			UserWritingAnswers item = new UserWritingAnswers();
			item.setContent(essay);
			item.setFeedback(node.path("feedback").asText());
			item.setTaskScore(new BigDecimal(node.path("task_score").asText("0")));
			item.setCoherenceScore(new BigDecimal(node.path("coherence_score").asText("0")));
			item.setLexicalScore(new BigDecimal(node.path("lexical_score").asText("0")));
			item.setGrammarScore(new BigDecimal(node.path("grammar_score").asText("0")));
			item.setOverallScore(new BigDecimal(node.path("overall_score").asText("0")));
			item.setDurationSeconds(duration);

			Users user = userService.getUserByUsername(principal.getName());
			item.setUser(user);
			item.setLesson(lesson);

			UserWritingAnswers saved = userWritingAnswerService.create(item);

			practiceSessionService.recordSession(user.getId(), lesson.getId(), saved.getOverallScore(), duration,
					saved.getFeedback());

			progressTrackerService.updateProgress(user.getId(), ProgressTrackers.Skill.WRITING,
					saved.getOverallScore());

			return ResponseEntity.ok(saved);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}
}

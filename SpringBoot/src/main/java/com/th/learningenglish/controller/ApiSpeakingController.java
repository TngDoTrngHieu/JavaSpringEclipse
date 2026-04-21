package com.th.learningenglish.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.pojo.UserSpeakingAnswers;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.repository.LessonRepository;
import com.th.learningenglish.repository.SectionRepository;
import com.th.learningenglish.repository.UserRepository;
import com.th.learningenglish.service.GeminiService;
import com.th.learningenglish.service.R2Service;
import com.th.learningenglish.service.UserSpeakingAnswerService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/speaking")
public class ApiSpeakingController {

	@Autowired
	private R2Service r2Service;

	@Autowired
	private GeminiService geminiService;

	@Autowired
	private UserSpeakingAnswerService speakingAnswerService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LessonRepository lessonRepository;

	@Autowired
	private SectionRepository sectionRepository;

	// Upload audio lên R2
	@PostMapping("/upload")
	public Map<String, Object> upload(@RequestParam("audio") MultipartFile audio) throws Exception {
		String url = r2Service.upload(audio);
		return Map.of("audioUrl", url);
	}

	// Submit transcript + audioUrl để chấm điểm
	@PostMapping("/submit")
	@Transactional
	public ResponseEntity<?> submit(@RequestBody Map<String, Object> req, Principal principal) {
		try {
			String transcript = (String) req.get("transcript");
			String audioUrl = (String) req.get("audioUrl");
			Long lessonId = Long.parseLong(req.get("lessonId").toString());
			int duration = req.containsKey("durationSeconds") ? Integer.parseInt(req.get("durationSeconds").toString())
					: 0;

			if (transcript == null || transcript.isBlank())
				return ResponseEntity.badRequest().body(Map.of("error", "Transcript is required"));

			// Fetch lesson + questions từ sections
			Lessons lesson = lessonRepository.findByIdWithLessonType(lessonId)
					.orElseThrow(() -> new RuntimeException("Lesson not found"));

			List<Sections> sections = sectionRepository.findByLesson_IdOrderByPositionAsc(lessonId);
			ObjectMapper mapper = new ObjectMapper();
			List<String> questions = new ArrayList<>();
			for (Sections s : sections) {
				if (s.getQuestion() == null || s.getQuestion().isBlank()) {
					continue;
				}
				try {
					JsonNode q = mapper.readTree(s.getQuestion());
					if (q.has("questions") && q.get("questions").isArray()) {
						q.get("questions").forEach(n -> questions.add(n.asText()));
					}
					if (q.has("cueCard")) {
						String prompt = q.get("cueCard").path("prompt").asText();
						if (prompt != null && !prompt.isBlank()) {
							questions.add(prompt);
						}
					}
				} catch (Exception ex) {
					questions.add(s.getQuestion());
				}
			}

			// Chấm điểm bằng Gemini
			String jsonResult = geminiService.evaluateSpeakingTranscript(lesson.getTitle(), questions, transcript);

			JsonNode node = mapper.readTree(jsonResult);

			UserSpeakingAnswers item = new UserSpeakingAnswers();
			item.setAudioUrl(audioUrl != null ? audioUrl : "");
			item.setTranscript(node.path("transcript").asText(transcript));
			item.setFeedback(node.path("feedback").asText());
			item.setPronunciationScore(new BigDecimal(node.path("pronunciation_score").asText("0")));
			item.setFluencyScore(new BigDecimal(node.path("fluency_score").asText("0")));
			item.setCoherenceScore(new BigDecimal(node.path("coherence_score").asText("0")));
			item.setLexicalScore(new BigDecimal(node.path("lexical_resource_score").asText("0")));
			item.setGrammarScore(new BigDecimal(node.path("grammar_score").asText("0")));
			item.setOverallScore(new BigDecimal(node.path("overall_score").asText("0")));
			item.setDurationSeconds(duration);

			Users user = userRepository.findByUsername(principal.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			item.setUser(user);
			item.setLesson(lesson);

			return ResponseEntity.ok(speakingAnswerService.create(item));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}
}
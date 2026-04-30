package com.th.learningenglish.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.UserAnswers;
import com.th.learningenglish.repository.UserAnswerRepository;

@Service
public class UserAnswerService {
	@Autowired
	private UserAnswerRepository repository;

	public List<UserAnswers> findAll() {
		return repository.findAll();
	}

	public UserAnswers findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("User answer not found"));
	}

	public UserAnswers create(UserAnswers item) {
		return repository.save(item);
	}

	public UserAnswers update(Long id, UserAnswers payload) {
		UserAnswers c = findById(id);
		c.setScore(payload.getScore());
		c.setSession(payload.getSession());
		c.setSection(payload.getSection());
		return repository.save(c);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public List<Map<String, Object>> getMyAnswerHistory(String username) {
		List<UserAnswers> answers = repository.findBySession_User_UsernameOrderByCreatedAtDesc(username);
		List<Map<String, Object>> history = new ArrayList<>();
		for (UserAnswers ua : answers) {
			var session = ua.getSession();
			var lesson = session != null ? session.getLesson() : null;
			var lessonType = lesson != null ? lesson.getLessonType() : null;
			Map<String, Object> item = new HashMap<>();
			item.put("id", ua.getId());
			item.put("score", ua.getScore());
			item.put("createdAt", ua.getCreatedAt());
			item.put("isCorrect", ua.getIsCorrect() != null ? ua.getIsCorrect() : false);
			item.put("answer", ua.getAnswer() != null ? ua.getAnswer() : "");
			item.put("sectionId", ua.getSection() != null ? ua.getSection().getId() : null);
			item.put("lessonId", lesson != null ? lesson.getId() : null);
			item.put("lessonTitle", lesson != null ? lesson.getTitle() : "Bài học");
			item.put("skill", lessonType != null && lessonType.getSkill() != null ? lessonType.getSkill().name() : "READING");
			item.put("durationSeconds", session != null ? session.getDurationSeconds() : 0);
			history.add(item);
		}
		return history;
	}
}

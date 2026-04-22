package com.th.learningenglish.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.pojo.PracticeSessions;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.repository.LessonRepository;
import com.th.learningenglish.repository.PracticeSessionRepository;
import com.th.learningenglish.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PracticeSessionService {
	@Autowired
	private PracticeSessionRepository repository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LessonRepository lessonRepository;

	public List<PracticeSessions> findAll() {
		return repository.findAll();
	}

	public PracticeSessions findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Practice session not found"));
	}

	public PracticeSessions create(PracticeSessions item) {
		return repository.save(item);
	}

	public PracticeSessions update(Long id, PracticeSessions payload) {
		PracticeSessions c = findById(id);
		c.setStartAt(payload.getStartAt());
		c.setDurationSeconds(payload.getDurationSeconds());
		c.setScore(payload.getScore());
		c.setFeedback(payload.getFeedback());
		c.setUser(payload.getUser());
		c.setLesson(payload.getLesson());
		return repository.save(c);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public List<PracticeSessions> findMySessions(String username) {
		Users user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return repository.findByUser_IdOrderByCreatedAtDesc(user.getId());
	}

	public List<PracticeSessions> findMySessionsBySkill(String username, LessonTypes.Skill skill) {
		Users user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return repository.findByUser_IdAndLesson_LessonType_Skill(user.getId(), skill);
	}

	@Transactional
	public PracticeSessions recordSession(Long userId, Long lessonId, BigDecimal score, int durationSeconds,
			String feedback) {
		Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		Lessons lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new RuntimeException("Lesson not found"));

		PracticeSessions session = new PracticeSessions();
		session.setUser(user);
		session.setLesson(lesson);
		session.setStartAt(LocalDateTime.now());
		session.setDurationSeconds(durationSeconds);
		session.setScore(score);
		session.setFeedback(feedback);
		return repository.save(session);
	}
}

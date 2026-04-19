package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.PracticeSessions;
import com.th.learningenglish.repository.PracticeSessionRepository;

@Service
public class PracticeSessionService {
	@Autowired
	private PracticeSessionRepository repository;

	public List<PracticeSessions> findAll() { return repository.findAll(); }
	public PracticeSessions findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Practice session not found")); }
	public PracticeSessions create(PracticeSessions item) { return repository.save(item); }
	public PracticeSessions update(Long id, PracticeSessions payload) {
		PracticeSessions c = findById(id);
		c.setStartAt(payload.getStartAt()); c.setDurationSeconds(payload.getDurationSeconds()); c.setScore(payload.getScore()); c.setFeedback(payload.getFeedback()); c.setUser(payload.getUser()); c.setLesson(payload.getLesson());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

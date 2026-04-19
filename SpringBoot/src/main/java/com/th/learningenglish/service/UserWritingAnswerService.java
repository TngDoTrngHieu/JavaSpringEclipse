package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.UserWritingAnswers;
import com.th.learningenglish.repository.UserWritingAnswerRepository;

@Service
public class UserWritingAnswerService {
	@Autowired
	private UserWritingAnswerRepository repository;

	public List<UserWritingAnswers> findAll() { return repository.findAll(); }
	public UserWritingAnswers findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("User writing answer not found")); }
	public UserWritingAnswers create(UserWritingAnswers item) { return repository.save(item); }
	public UserWritingAnswers update(Long id, UserWritingAnswers payload) {
		UserWritingAnswers c = findById(id);
		c.setContent(payload.getContent()); c.setFeedback(payload.getFeedback()); c.setTaskScore(payload.getTaskScore()); c.setCoherenceScore(payload.getCoherenceScore()); c.setLexicalScore(payload.getLexicalScore()); c.setGrammarScore(payload.getGrammarScore()); c.setOverallScore(payload.getOverallScore()); c.setDurationSeconds(payload.getDurationSeconds()); c.setUser(payload.getUser()); c.setLesson(payload.getLesson());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

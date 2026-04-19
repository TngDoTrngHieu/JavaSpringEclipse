package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.UserSpeakingAnswers;
import com.th.learningenglish.repository.UserSpeakingAnswerRepository;

@Service
public class UserSpeakingAnswerService {
	@Autowired
	private UserSpeakingAnswerRepository repository;

	public List<UserSpeakingAnswers> findAll() { return repository.findAll(); }
	public UserSpeakingAnswers findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("User speaking answer not found")); }
	public UserSpeakingAnswers create(UserSpeakingAnswers item) { return repository.save(item); }
	public UserSpeakingAnswers update(Long id, UserSpeakingAnswers payload) {
		UserSpeakingAnswers c = findById(id);
		c.setAudioUrl(payload.getAudioUrl()); c.setTranscript(payload.getTranscript()); c.setFeedback(payload.getFeedback()); c.setPronunciationScore(payload.getPronunciationScore()); c.setFluencyScore(payload.getFluencyScore()); c.setCoherenceScore(payload.getCoherenceScore()); c.setLexicalScore(payload.getLexicalScore()); c.setGrammarScore(payload.getGrammarScore()); c.setOverallScore(payload.getOverallScore()); c.setDurationSeconds(payload.getDurationSeconds()); c.setUser(payload.getUser()); c.setLesson(payload.getLesson());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

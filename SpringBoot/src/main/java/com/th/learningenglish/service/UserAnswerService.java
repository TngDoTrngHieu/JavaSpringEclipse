package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.UserAnswers;
import com.th.learningenglish.repository.UserAnswerRepository;

@Service
public class UserAnswerService {
	@Autowired
	private UserAnswerRepository repository;

	public List<UserAnswers> findAll() { return repository.findAll(); }
	public UserAnswers findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("User answer not found")); }
	public UserAnswers create(UserAnswers item) { return repository.save(item); }
	public UserAnswers update(Long id, UserAnswers payload) {
		UserAnswers c = findById(id);
		c.setScore(payload.getScore()); c.setSession(payload.getSession()); c.setSection(payload.getSection());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

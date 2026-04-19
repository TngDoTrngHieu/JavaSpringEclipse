package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.ProgressTrackers;
import com.th.learningenglish.repository.ProgressTrackerRepository;

@Service
public class ProgressTrackerService {
	@Autowired
	private ProgressTrackerRepository repository;

	public List<ProgressTrackers> findAll() { return repository.findAll(); }
	public ProgressTrackers findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Progress tracker not found")); }
	public ProgressTrackers create(ProgressTrackers item) { return repository.save(item); }
	public ProgressTrackers update(Long id, ProgressTrackers payload) {
		ProgressTrackers c = findById(id);
		c.setSkill(payload.getSkill()); c.setScore(payload.getScore()); c.setUser(payload.getUser()); c.setStudyPlan(payload.getStudyPlan());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.StudyPlans;
import com.th.learningenglish.repository.StudyPlanRepository;

@Service
public class StudyPlanService {
	@Autowired
	private StudyPlanRepository repository;

	public List<StudyPlans> findAll() {
		return repository.findAll();
	}

	public StudyPlans findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Study plan not found"));
	}

	public StudyPlans create(StudyPlans item) {
		return repository.save(item);
	}

	public StudyPlans update(Long id, StudyPlans payload) {
		StudyPlans current = findById(id);
		current.setDurationDays(payload.getDurationDays());
		current.setGoalScore(payload.getGoalScore());
		current.setUser(payload.getUser());
		return repository.save(current);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
}

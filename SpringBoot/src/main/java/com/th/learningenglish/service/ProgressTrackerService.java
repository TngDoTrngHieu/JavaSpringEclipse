package com.th.learningenglish.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.ProgressTrackers;
import com.th.learningenglish.pojo.StudyPlans;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.repository.ProgressTrackerRepository;
import com.th.learningenglish.repository.StudyPlanRepository;
import com.th.learningenglish.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ProgressTrackerService {
	@Autowired
	private ProgressTrackerRepository repository;

	@Autowired
	private StudyPlanRepository studyPlanRepository;
	@Autowired
	private UserRepository userRepository;

	public List<ProgressTrackers> findAll() {
		return repository.findAll();
	}

	public ProgressTrackers findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Progress tracker not found"));
	}

	public ProgressTrackers create(ProgressTrackers item) {
		return repository.save(item);
	}

	public ProgressTrackers update(Long id, ProgressTrackers payload) {
		ProgressTrackers current = findById(id);
		current.setSkill(payload.getSkill());
		current.setScore(payload.getScore());
		current.setUser(payload.getUser());
		current.setStudyPlan(payload.getStudyPlan());
		return repository.save(current);
	}

	@Transactional
	public void updateProgress(Long userId, ProgressTrackers.Skill skill, BigDecimal newScore) {
		StudyPlans plan = studyPlanRepository.findTopByUser_IdOrderByCreatedAtDesc(userId).orElseGet(() -> {
			Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
			StudyPlans defaultPlan = new StudyPlans();
			defaultPlan.setUser(user);
			defaultPlan.setDurationDays(30);
			defaultPlan.setGoalScore(null);
			return studyPlanRepository.save(defaultPlan);
		});

		Optional<ProgressTrackers> existing = repository.findByUser_IdAndStudyPlan_IdAndSkill(userId, plan.getId(),
				skill);

		if (existing.isPresent()) {
			ProgressTrackers tracker = existing.get();
			BigDecimal avg = tracker.getScore().add(newScore).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
			tracker.setScore(avg);
			repository.save(tracker);
		} else {
			Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
			ProgressTrackers tracker = new ProgressTrackers();
			tracker.setUser(user);
			tracker.setStudyPlan(plan);
			tracker.setSkill(skill);
			tracker.setScore(newScore);
			repository.save(tracker);
		}
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
}
